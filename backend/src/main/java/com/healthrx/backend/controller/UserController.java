package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.InitAndConfigResponse;
import com.healthrx.backend.api.external.Token;
import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.api.external.VerificationDataResponse;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User controller", description = "Kontroler do zarządzania danymi użytkownika w kontekście weryfikacji i inicjalizacji")
public class UserController {
    private final UserService userService;

    @GetMapping("/initAndConfig")
    @Operation(summary = "Pobranie danych inicjalizacyjnych i konfiguracyjnych", description = "Pobranie danych inicjalizacyjnych i konfiguracyjnych")
    public ResponseEntity<InitAndConfigResponse> getInitAndConfigData() {
        return ResponseEntity.ok(userService.getInitAndConfigData());
    }

    @PostMapping("/getVerificationData")
    @Operation(summary = "Pobranie danych do zweryfikowania konta użytkownika", description = "Pobranie danych do zweryfikowania konta użytkownika")
    public ResponseEntity<VerificationDataResponse> getVerificationData(@RequestBody Token request) {
        return ResponseEntity.ok(userService.getVerificationData(request));
    }

    @PostMapping("/verification")
    @Operation(summary = "Weryfikacja konta użytkownika", description = "Weryfikacja konta użytkownika")
    public ResponseEntity<Void> verifyUser(@RequestBody UserVerificationRequest request) {
        if (userService.verifyUser(request) == null) {
            throw BusinessErrorCodes.INVALID_VERIFICATION.getError();
        }

        return ResponseEntity.ok().build();
    }
}
