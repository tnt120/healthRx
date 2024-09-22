package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.invitation.InvitationRequest;
import com.healthrx.backend.api.external.invitation.InvitationResponse;
import com.healthrx.backend.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/friendship")
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

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
