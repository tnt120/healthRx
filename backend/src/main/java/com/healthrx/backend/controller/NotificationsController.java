package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.notifications.DiscardNotificationsRequest;
import com.healthrx.backend.api.external.notifications.NotificationDTO;
import com.healthrx.backend.service.NotificationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications controller", description = "Kontroler do zarządzania powiadomieniami")
public class NotificationsController {
    private final NotificationsService notificationsService;

    @GetMapping
    @Operation(summary = "Pobranie wszystkich powiadomień", description = "Pobranie wszystkich powiadomień")
    public ResponseEntity<List<NotificationDTO>> getNotificationsAll() {
        return ResponseEntity.ok(notificationsService.getNotificationsAll());
    }

    @PostMapping("/discard")
    @Operation(summary = "Usunięcie odczytanego powiadomienia", description = "Usunięcie odczytanego powiadomienia")
    public ResponseEntity<Void> discardNotifications(@RequestBody DiscardNotificationsRequest req) {
        return ResponseEntity.ok(notificationsService.discardNotification(req));
    }
}
