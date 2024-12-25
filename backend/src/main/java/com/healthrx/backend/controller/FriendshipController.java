package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
@Tag(name = "Friendship controller", description = "Controller for managing friendships (collaborations)")
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping(("/pending"))
    @Operation(summary = "Fetching invitations waiting for acceptance", description = "Fetching invitations waiting for acceptance")
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsPending() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.WAITING));
    }

    @GetMapping("/rejected")
    @Operation(summary = "Fetching rejected invitations", description = "Fetching rejected invitations")
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsRejected() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.REJECTED));
    }

    @GetMapping("/accepted")
    @Operation(summary = "Fetching a list of collaborators (friends)", description = "Fetching accepted invitations")
    public ResponseEntity<PageResponse<FriendshipResponse>> getFriendships(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "asc", required = false) String order,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName
    ) {
        return ResponseEntity.ok(friendshipService.getFriendships(page, size, sortBy, order, firstName, lastName));
    }

    @PostMapping("/permissions/{id}")
    @Operation(summary = "Change of access rights to health information for a given acquaintance (cooperation)", description = "Changing friendship permissions")
    public ResponseEntity<FriendshipPermissions> updatePermissions(@PathVariable String id, @RequestBody FriendshipPermissions request) {
        return ResponseEntity.ok(friendshipService.updatePermissions(id, request));
    }

    @PostMapping("/invite")
    @Operation(summary = "Sending an invitation to cooperate", description = "Sending an invitation to cooperate")
    public ResponseEntity<InvitationResponse> sendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.sendInvitation(request));
    }

    @PostMapping("/accept")
    @Operation(summary = "Acceptance of invitation to cooperation", description = "Acceptance of invitation to cooperation")
    public ResponseEntity<InvitationResponse> acceptInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.acceptInvitation(request));
    }

    @PostMapping("/reject")
    @Operation(summary = "Rejecting an invitation to cooperate", description = "Rejecting an invitation to cooperate")
    public ResponseEntity<InvitationResponse> rejectInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.rejectInvitation(request));
    }

    @PostMapping("/resend")
    @Operation(summary = "Resending invitation to collaborate", description = "Resending invitation to collaborate")
    public ResponseEntity<InvitationResponse> resendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.resendInvitation(request));
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Deleting acquaintances (cooperations)", description = "Deleting acquaintances (cooperations)")
    public ResponseEntity<InvitationResponse> cancelInvitation(@PathVariable String id) {
        return ResponseEntity.ok(friendshipService.removeInvitation(id));
    }
}
