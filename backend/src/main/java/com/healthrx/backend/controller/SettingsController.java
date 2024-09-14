package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.settings.NotificationsData;
import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;

    @PostMapping("/personalDataChange")
    public ResponseEntity<Void> personalDataChange(@RequestBody PersonalDataChangeRequest request) {
        return ResponseEntity.ok(settingsService.personalDataChange(request));
    }

    @PostMapping("/passwordChange")
    public ResponseEntity<Void> passwordChange(@RequestBody PasswordChangeRequest request) {
        return ResponseEntity.ok(settingsService.passwordChange(request));
    }

    @PutMapping("/notificationsChange")
    public ResponseEntity<NotificationsData> notificationsChange(@RequestBody NotificationsData request) {
        return ResponseEntity.ok(settingsService.notificationsChange(request));
    }
}
