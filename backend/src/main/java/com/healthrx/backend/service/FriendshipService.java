package com.healthrx.backend.service;

import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;

import java.util.List;

public interface FriendshipService {
    List<FriendshipResponse> getFriendships(boolean getPendingAndRejected);
    InvitationResponse sendInvitation(InvitationRequest request);
    InvitationResponse acceptInvitation(InvitationRequest request);
    InvitationResponse rejectInvitation(InvitationRequest request);
    InvitationResponse resendInvitation(InvitationRequest request);
    InvitationResponse removeInvitation(String id);
}
