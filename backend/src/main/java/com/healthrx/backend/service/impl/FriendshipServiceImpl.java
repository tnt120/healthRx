package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.mapper.FriendshipMapper;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.repository.MessageRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.FriendshipService;
import com.healthrx.backend.specification.FriendshipSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Supplier<User> principalSupplier;
    private final FriendshipMapper friendshipMapper;

    @Override
    @Transactional
    public List<FriendshipResponse> getFriendships(FriendshipStatus status) {
        User user = principalSupplier.get();

        if (user.getRole() == Role.USER) {
            return friendshipRepository.getFriendshipsByUserId(user.getId())
                    .stream()
                    .filter(friendship -> switch (status) {
                        case WAITING -> friendship.getStatus() == FriendshipStatus.WAITING;
                        case ACCEPTED -> friendship.getStatus() == FriendshipStatus.ACCEPTED;
                        case REJECTED -> friendship.getStatus() == FriendshipStatus.REJECTED;
                    })
                    .map(friendship -> FriendshipResponse.builder()
                            .friendshipId(friendship.getId())
                            .user(friendshipMapper.mapUser(friendship.getDoctor()))
                            .status(friendship.getStatus())
                            .updatedAt(friendship.getUpdatedAt())
                            .permissions(friendshipMapper.mapPermissions(friendship))
                            .build()
                    )
                    .toList();
        } else if (user.getRole() == Role.DOCTOR) {
            return friendshipRepository.getFriendshipsByDoctorId(user.getId())
                    .stream()
                    .filter(friendship -> switch (status) {
                        case WAITING -> friendship.getStatus() == FriendshipStatus.WAITING;
                        case ACCEPTED -> friendship.getStatus() == FriendshipStatus.ACCEPTED;
                        case REJECTED -> friendship.getStatus() == FriendshipStatus.REJECTED;
                    })
                    .map(friendship -> FriendshipResponse.builder()
                            .friendshipId(friendship.getId())
                            .user(friendshipMapper.mapUser(friendship.getUser()))
                            .status(friendship.getStatus())
                            .updatedAt(friendship.getUpdatedAt())
                            .permissions(friendshipMapper.mapPermissions(friendship))
                            .build()
                    )
                    .toList();
        }

        throw USER_NOT_PERMITTED.getError();
    }

    @Override
    @Transactional
    public PageResponse<FriendshipResponse> getFriendships(Integer page, Integer size, String sortBy, String order, String firstName, String lastName) {
        User user = principalSupplier.get();

        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Friendship> specification = Specification.where(FriendshipSpecification.isAccepted());
        specification = Specification.where(FriendshipSpecification.isMyFriendship(user.getId(), user.getRole() == Role.DOCTOR)).and(specification);

        if (user.getRole() == Role.USER) {
            if (firstName != null) specification = specification.and(FriendshipSpecification.doctorFirstNameContains(firstName));
            if (lastName != null) specification = specification.and(FriendshipSpecification.doctorLastNameContains(lastName));
        } else if (user.getRole() == Role.DOCTOR) {
            if (firstName != null) specification = specification.and(FriendshipSpecification.userFirstNameContains(firstName));
            if (lastName != null) specification = specification.and(FriendshipSpecification.userLastNameContains(lastName));
        } else {
            throw USER_NOT_PERMITTED.getError();
        }

        Page<Friendship> friendships = friendshipRepository.findAll(specification, pageable);

        List<FriendshipResponse> friendshipsResponse = friendships.getContent()
                .stream()
                .map(friendship -> {
                    if (user.getRole() == Role.USER) {
                        return friendshipMapper.mapFriendship(friendship, friendship.getDoctor());
                    } else {
                        return friendshipMapper.mapFriendship(friendship, friendship.getUser());
                    }
                })
                .toList();

        return new PageResponse<FriendshipResponse>()
                .setContent(friendshipsResponse)
                .setCurrentPage(friendships.getNumber())
                .setPageSize(friendships.getSize())
                .setTotalElements(friendships.getTotalElements())
                .setLast(friendships.isLast())
                .setFirst(friendships.isFirst());
    }

    @Override
    public FriendshipPermissions updatePermissions(String friendshipId, FriendshipPermissions request) {
        User user = principalSupplier.get();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(INVITATION_NOT_FOUND::getError);

        if (!friendship.getUser().getId().equals(user.getId())) {
            throw USER_NOT_PERMITTED.getError();
        }

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw INVITATION_NOT_ACCEPTED.getError();
        }

        friendship.setParametersAccess(request.getParametersAccess());
        friendship.setActivitiesAccess(request.getActivitiesAccess());
        friendship.setUserMedicineAccess(request.getUserMedicineAccess());

        friendshipRepository.save(friendship);

        return friendshipMapper.mapPermissions(friendship);
    }

    @Override
    public Friendship getFriendshipByUsers(String doctor, String user) {
        return friendshipRepository.getFriendshipByUserIdAndDoctorId(user, doctor)
                .orElseThrow(FRIENDSHIP_NOT_FOUND::getError);
    }

    @Override
    public InvitationResponse sendInvitation(InvitationRequest request) {
        User user = principalSupplier.get();

        if (user.getRole() != Role.USER) throw USER_NOT_PERMITTED.getError();

        friendshipRepository.getFriendshipByUserIdAndDoctorId(
                user.getId(),
                request.getTargetDoctorId()
        ).ifPresent(friendship -> {
            throw INVITATION_ALREADY_EXISTS.getError();
        });

        User doctor = userRepository.findById(request.getTargetDoctorId())
                .orElseThrow(USER_NOT_FOUND::getError);

        if (doctor.getDoctorDetails() == null || !doctor.getDoctorDetails().getIsVerifiedDoctor()) throw WRONG_INVITATION_TARGET.getError();

        String invitationId = this.friendshipRepository.save(
                Friendship.builder()
                        .user(user)
                        .doctor(doctor)
                        .status(FriendshipStatus.WAITING)
                        .parametersAccess(false)
                        .activitiesAccess(false)
                        .userMedicineAccess(false)
                        .build()
        ).getId();

        return InvitationResponse.builder()
                .friendshipId(invitationId)
                .build();
    }

    @Override
    public InvitationResponse acceptInvitation(InvitationRequest request) {
        User user = principalSupplier.get();

        Friendship friendship = getDoctorFriendship(request, user.getId());

        checkIfAcceptedOrRejected(friendship);

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        friendshipRepository.save(friendship);

        return InvitationResponse.builder()
                .friendshipId(friendship.getId())
                .build();
    }

    @Override
    public InvitationResponse rejectInvitation(InvitationRequest request) {
        User user = principalSupplier.get();

        Friendship friendship = getDoctorFriendship(request, user.getId());

        checkIfAcceptedOrRejected(friendship);

        friendship.setStatus(FriendshipStatus.REJECTED);

        friendshipRepository.save(friendship);

        return InvitationResponse.builder()
                .friendshipId(friendship.getId())
                .build();
    }

    @Override
    public InvitationResponse resendInvitation(InvitationRequest request) {
        User user = principalSupplier.get();

        Friendship friendship = friendshipRepository.findById(request.getInvitationId()).orElseThrow(INVITATION_NOT_FOUND::getError);

        if (!friendship.getUser().getId().equals(user.getId())) throw USER_NOT_PERMITTED.getError();

        if (friendship.getStatus() == FriendshipStatus.ACCEPTED) throw INVITATION_ALREADY_ACCEPTED.getError();

        friendship.setStatus(FriendshipStatus.WAITING);

        friendshipRepository.save(friendship);

        return InvitationResponse.builder()
                .friendshipId(friendship.getId())
                .build();
    }

    @Override
    @Transactional
    public InvitationResponse removeInvitation(String friendshipId) {
        User user = principalSupplier.get();

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(INVITATION_NOT_FOUND::getError);

        if (!friendship.getUser().getId().equals(user.getId()) && !friendship.getDoctor().getId().equals(user.getId())) throw USER_NOT_PERMITTED.getError();

        messageRepository.deleteAllByFriendshipId(friendshipId);

        friendshipRepository.delete(friendship);

        return InvitationResponse.builder()
                .friendshipId(friendshipId)
                .build();
    }

    @Override
    @Transactional
    public void removeFriendshipByUser(String id, Boolean isDoctor) {
        Optional<Friendship> friendship;

        if (isDoctor) {
            friendship = friendshipRepository.getFriendshipByDoctorId(id);
        } else {
            friendship = friendshipRepository.getFriendshipByUserId(id);
        }

        if (friendship.isEmpty()) return;
        messageRepository.deleteAllByFriendshipId(friendship.get().getId());
        friendshipRepository.delete(friendship.get());
    }

    private Friendship getDoctorFriendship(InvitationRequest request, String doctorId) {
        Friendship friendship = friendshipRepository.findById(request.getInvitationId())
                .orElseThrow(INVITATION_NOT_FOUND::getError);

        if (!friendship.getDoctor().getId().equals(doctorId)) throw USER_NOT_PERMITTED.getError();

        return friendship;
    }

    private void checkIfAcceptedOrRejected(Friendship friendship) {
        if (friendship.getStatus() == FriendshipStatus.REJECTED || friendship.getStatus() == FriendshipStatus.ACCEPTED) throw INVITATION_ALREADY_ACCEPTED_REJECTED.getError();
    }
}
