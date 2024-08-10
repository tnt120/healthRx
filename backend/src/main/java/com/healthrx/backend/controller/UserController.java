package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.InitAndConfigResponse;
import com.healthrx.backend.api.external.Token;
import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.api.external.VerificationDataResponse;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/getVerificationData")
    public ResponseEntity<VerificationDataResponse> getVerificationData(@RequestBody Token request) {
        return ResponseEntity.ok(userService.getVerificationData(request));
    }

    @PostMapping("/verification")
    public ResponseEntity<Void> verifyUser(@RequestBody UserVerificationRequest request) {
        if (userService.verifyUser(request) == null) {
            throw BusinessErrorCodes.INVALID_VERIFICATION.getError();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/initAndConfig")
    public ResponseEntity<InitAndConfigResponse> getInitAndConfigData() {
        return ResponseEntity.ok(userService.getInitAndConfigData());
    }

}
