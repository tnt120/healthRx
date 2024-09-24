package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.FriendshipPermissions;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.invitation.FriendshipResponse;
import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.api.internal.enums.FriendshipStatus;
import com.healthrx.backend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping(("/pending"))
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsPending() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.WAITING));
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsRejected() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.REJECTED));
    }

    @GetMapping("/accepted")
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
    public ResponseEntity<FriendshipPermissions> updatePermissions(@PathVariable String id, @RequestBody FriendshipPermissions request) {
        return ResponseEntity.ok(friendshipService.updatePermissions(id, request));
    }

    @PostMapping("/invite")
    public ResponseEntity<InvitationResponse> sendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.sendInvitation(request));
    }

    @PostMapping("/accept")
    public ResponseEntity<InvitationResponse> acceptInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.acceptInvitation(request));
    }

    @PostMapping("/reject")
    public ResponseEntity<InvitationResponse> rejectInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.rejectInvitation(request));
    }

    @PostMapping("/resend")
    public ResponseEntity<InvitationResponse> resendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.resendInvitation(request));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<InvitationResponse> cancelInvitation(@PathVariable String id) {
        return ResponseEntity.ok(friendshipService.removeInvitation(id));
    }
}
