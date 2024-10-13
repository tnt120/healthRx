package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PatchMapping("/verifyDoctor")
    public ResponseEntity<Void> verifyDoctor(@RequestBody DoctorVerificationRequest req) {
        return ResponseEntity.ok(adminService.verifyDoctor(req));
    }
}
