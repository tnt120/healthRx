package com.healthrx.backend.service;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;

public interface AdminService {
    Void verifyDoctor(DoctorVerificationRequest req);
    PageResponse<DoctorResponse> getApprovals(Integer page, Integer size);
}
