package com.healthrx.backend.service.impl;

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
