package com.healthrx.backend.service;

import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;

public interface FriendshipService {
    InvitationResponse sendInvitation(InvitationRequest request);
    InvitationResponse acceptInvitation(InvitationRequest request);
    InvitationResponse rejectInvitation(InvitationRequest request);
    InvitationResponse resendInvitation(InvitationRequest request);
    InvitationResponse removeInvitation(String id);
}
