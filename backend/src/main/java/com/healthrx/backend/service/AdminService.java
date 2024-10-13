package com.healthrx.backend.service;

import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;

public interface AdminService {
    Void verifyDoctor(DoctorVerificationRequest req);
}
