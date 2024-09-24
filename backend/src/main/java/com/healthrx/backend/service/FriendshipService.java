package com.healthrx.backend.service;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;

import java.util.List;

public interface FriendshipService {
    List<FriendshipResponse> getFriendships(FriendshipStatus status);
    PageResponse<FriendshipResponse> getFriendships(Integer page, Integer size, String sortBy, String order, String firstName, String lastName);
    FriendshipPermissions updatePermissions(String friendshipId, FriendshipPermissions request);
    InvitationResponse sendInvitation(InvitationRequest request);
    InvitationResponse acceptInvitation(InvitationRequest request);
    InvitationResponse rejectInvitation(InvitationRequest request);
    InvitationResponse resendInvitation(InvitationRequest request);
    InvitationResponse removeInvitation(String id);
}
