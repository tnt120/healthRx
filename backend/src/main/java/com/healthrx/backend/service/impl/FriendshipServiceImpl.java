package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.chat.Friendship;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.repository.FriendshipRepository;
import com.healthrx.backend.repository.MessageRepository;
import com.healthrx.backend.repository.UserRepository;
import com.healthrx.backend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<FriendshipResponse> getFriendships(boolean getPendingAndRejected) {
        User user = principalSupplier.get();

        if (user.getRole() == Role.USER) {
            return friendshipRepository.getFriendshipsByUserId(user.getId())
                    .stream()
                    .filter(friendship -> {
                        if (getPendingAndRejected) {
                            return friendship.getStatus() == FriendshipStatus.WAITING || friendship.getStatus() == FriendshipStatus.REJECTED;
                        } else {
                            return friendship.getStatus() == FriendshipStatus.ACCEPTED;
                        }
                    })
                    .map(friendship -> FriendshipResponse.builder()
                            .friendshipId(friendship.getId())
                            .user(UserResponse.builder()
                                    .firstName(friendship.getDoctor().getFirstName())
                                    .lastName(friendship.getDoctor().getLastName())
                                    .pictureUrl(friendship.getDoctor().getPictureUrl())
                                    .build()
                            )
                            .status(friendship.getStatus())
                            .updatedAt(friendship.getUpdatedAt())
                            .build()
                    )
                    .toList();
        } else if (user.getRole() == Role.DOCTOR) {
            return friendshipRepository.getFriendshipsByDoctorId(user.getId())
                    .stream()
                    .filter(friendship -> {
                        if (getPendingAndRejected) {
                            return friendship.getStatus() == FriendshipStatus.WAITING || friendship.getStatus() == FriendshipStatus.REJECTED;
                        } else {
                            return friendship.getStatus() == FriendshipStatus.ACCEPTED;
                        }
                    })
                    .map(friendship -> FriendshipResponse.builder()
                            .friendshipId(friendship.getId())
                            .user(UserResponse.builder()
                                    .firstName(friendship.getUser().getFirstName())
                                    .lastName(friendship.getUser().getLastName())
                                    .pictureUrl(friendship.getUser().getPictureUrl())
                                    .build()
                            )
                            .status(friendship.getStatus())
                            .updatedAt(friendship.getUpdatedAt())
                            .build()
                    )
                    .toList();
        }

        throw USER_NOT_PERMITTED.getError();
    }

    @Override
    public InvitationResponse sendInvitation(InvitationRequest request) {
        User user = principalSupplier.get();

        if (user.isVerifiedDoctor() || user.getRole() != Role.USER) throw USER_NOT_PERMITTED.getError();

        friendshipRepository.getFriendshipByUserIdAndDoctorId(
                user.getId(),
                request.getTargetDoctorId()
        ).ifPresent(friendship -> {
            throw INVITATION_ALREADY_EXISTS.getError();
        });

        User doctor = userRepository.findById(request.getTargetDoctorId())
                .orElseThrow(USER_NOT_FOUND::getError);

        if (!doctor.isVerifiedDoctor()) throw WRONG_INVITATION_TARGET.getError();

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
