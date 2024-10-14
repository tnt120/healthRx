package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
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

    @GetMapping("/approvals")
    public ResponseEntity<PageResponse<DoctorResponse>> getApprovals(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size
    ) {
        return ResponseEntity.ok(adminService.getApprovals(page, size));
    }

    @PatchMapping("/verifyDoctor")
    public ResponseEntity<Void> verifyDoctor(@RequestBody DoctorVerificationRequest req) {
        return ResponseEntity.ok(adminService.verifyDoctor(req));
    }
}
