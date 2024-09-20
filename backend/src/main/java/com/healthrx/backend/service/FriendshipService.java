package com.healthrx.backend.service;

import com.healthrx.backend.api.external.invitation.InvitationRequest;

public interface FriendshipService {
    Void sendInvitation(InvitationRequest request);
    Void acceptInvitation(InvitationRequest request);
    Void rejectInvitation(InvitationRequest request);
    Void resendInvitation(InvitationRequest request);
    Void removeInvitation(String id);
}
