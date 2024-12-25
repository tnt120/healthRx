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
@Tag(name = "User controller", description = "Controller for managing user data in the context of verification and initialization")
public class UserController {
    private final UserService userService;

    @GetMapping("/initAndConfig")
    @Operation(summary = "Fetching initialization and configuration data", description = "Fetching initialization and configuration data")
    public ResponseEntity<InitAndConfigResponse> getInitAndConfigData() {
        return ResponseEntity.ok(userService.getInitAndConfigData());
    }

    @PostMapping("/getVerificationData")
    @Operation(summary = "Fetching data to verify the user account", description = "Fetching data to verify the user account")
    public ResponseEntity<VerificationDataResponse> getVerificationData(@RequestBody Token request) {
        return ResponseEntity.ok(userService.getVerificationData(request));
    }

    @PostMapping("/verification")
    @Operation(summary = "Verification user account", description = "Verification user account")
    public ResponseEntity<Void> verifyUser(@RequestBody UserVerificationRequest request) {
        if (userService.verifyUser(request) == null) {
            throw BusinessErrorCodes.INVALID_VERIFICATION.getError();
        }

        return ResponseEntity.ok().build();
    }
}
