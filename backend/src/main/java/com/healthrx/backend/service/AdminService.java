package com.healthrx.backend.service;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.internal.User;

public interface AdminService {
    Void verifyDoctor(DoctorVerificationRequest req);
    PageResponse<DoctorResponse> getApprovals(Integer page, Integer size);
    User checkPermissions();
    void checkHeadAdminPermissions(User user);
}
