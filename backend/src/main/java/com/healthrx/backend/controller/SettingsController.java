package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Tag(name = "Settings controller", description = "Kontroler do zarządzania ustawieniami użytkownika")
public class SettingsController {
    private final SettingsService settingsService;

    @PostMapping("/personalDataChange")
    @Operation(summary = "Zmiana danych osobowych", description = "Zmiana danych osobowych - imię, nazwisko, email, numer telefonu, data urodzenia, płeć, wzrost")
    public ResponseEntity<Void> personalDataChange(@RequestBody PersonalDataChangeRequest request) {
        return ResponseEntity.ok(settingsService.personalDataChange(request));
    }

    @PostMapping("/passwordChange")
    @Operation(summary = "Zmiana hasła", description = "Zmiana hasła")
    public ResponseEntity<Void> passwordChange(@RequestBody PasswordChangeRequest request) {
        return ResponseEntity.ok(settingsService.passwordChange(request));
    }

    @PutMapping("/notificationsChange")
    @Operation(summary = "Zmiana ustawień powiadomień", description = "Zmiana ustawień powiadomień - powiadomienia email, powiadomienia push")
    public ResponseEntity<NotificationsData> notificationsChange(@RequestBody NotificationsData request) {
        return ResponseEntity.ok(settingsService.notificationsChange(request));
    }
}
