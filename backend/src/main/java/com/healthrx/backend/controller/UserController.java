package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.UserVerificationRequest;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/verification")
    public ResponseEntity<Void> verifyUser(@RequestBody UserVerificationRequest request) {
        if (userService.verifyUser(request) == null) {
            throw BusinessErrorCodes.VERIFICATION_ERROR.getError();
        }

        return ResponseEntity.ok().build();
    }
}
