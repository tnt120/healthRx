package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.settings.PasswordChangeRequest;
import com.healthrx.backend.api.external.settings.PersonalDataChangeRequest;
import com.healthrx.backend.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
