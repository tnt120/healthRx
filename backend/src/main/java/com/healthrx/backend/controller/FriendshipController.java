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
@Tag(name = "Friendship controller", description = "Kontroler do zarządzania znajomościami (współpracami)")
public class FriendshipController {
    private final FriendshipService friendshipService;

    @GetMapping(("/pending"))
    @Operation(summary = "Pobranie zaproszeń oczekujących na akceptację", description = "Pobranie zaproszeń oczekujących na akceptację")
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsPending() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.WAITING));
    }

    @GetMapping("/rejected")
    @Operation(summary = "Pobranie zaproszeń odrzuconych", description = "Pobranie zaproszeń odrzuconych")
    public ResponseEntity<List<FriendshipResponse>> getFriendshipsRejected() {
        return ResponseEntity.ok(friendshipService.getFriendships(FriendshipStatus.REJECTED));
    }

    @GetMapping("/accepted")
    @Operation(summary = "Pobranie listy współpracujących (znajomych)", description = "Pobranie zaproszeń zaakceptowanych")
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
    @Operation(summary = "Zmiana uprawnień dostępu do informacji zdrowotnych dla podnej znajomości (współpracy)", description = "Zmiana uprawnień znajomości")
    public ResponseEntity<FriendshipPermissions> updatePermissions(@PathVariable String id, @RequestBody FriendshipPermissions request) {
        return ResponseEntity.ok(friendshipService.updatePermissions(id, request));
    }

    @PostMapping("/invite")
    @Operation(summary = "Wysłanie zaproszenia do współpracy", description = "Wysłanie zaproszenia do współpracy")
    public ResponseEntity<InvitationResponse> sendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.sendInvitation(request));
    }

    @PostMapping("/accept")
    @Operation(summary = "Akceptacja zaproszenia do współpracy", description = "Akceptacja zaproszenia do współpracy")
    public ResponseEntity<InvitationResponse> acceptInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.acceptInvitation(request));
    }

    @PostMapping("/reject")
    @Operation(summary = "Odrzucenie zaproszenia do współpracy", description = "Odrzucenie zaproszenia do współpracy")
    public ResponseEntity<InvitationResponse> rejectInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.rejectInvitation(request));
    }

    @PostMapping("/resend")
    @Operation(summary = "Ponowne wysłanie zaproszenia do współpracy", description = "Ponowne wysłanie zaproszenia do współpracy")
    public ResponseEntity<InvitationResponse> resendInvitation(@RequestBody InvitationRequest request) {
        return ResponseEntity.ok(friendshipService.resendInvitation(request));
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Usunięcie znajomości (współpracy)", description = "Usunięcie znajomości (współpracy)")
    public ResponseEntity<InvitationResponse> cancelInvitation(@PathVariable String id) {
        return ResponseEntity.ok(friendshipService.removeInvitation(id));
    }
}
