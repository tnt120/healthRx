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
@Tag(name = "Settings controller", description = "Controller for managing user settings")
public class SettingsController {
    private final SettingsService settingsService;

    @PostMapping("/personalDataChange")
    @Operation(summary = "Change of personal data", description = "Change of personal data - name, surname, email, phone number, date of birth, gender, height")
    public ResponseEntity<Void> personalDataChange(@RequestBody PersonalDataChangeRequest request) {
        return ResponseEntity.ok(settingsService.personalDataChange(request));
    }

    @PostMapping("/passwordChange")
    @Operation(summary = "Change password", description = "Change password")
    public ResponseEntity<Void> passwordChange(@RequestBody PasswordChangeRequest request) {
        return ResponseEntity.ok(settingsService.passwordChange(request));
    }

    @PutMapping("/notificationsChange")
    @Operation(summary = "Changing notification settings", description = "Changing notification settings - email notifications, push notifications")
    public ResponseEntity<NotificationsData> notificationsChange(@RequestBody NotificationsData request) {
        return ResponseEntity.ok(settingsService.notificationsChange(request));
    }
}
