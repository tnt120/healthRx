package com.healthrx.backend.service;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.ReVerifyDoctorRequest;

public interface DoctorService {
    PageResponse<DoctorResponse> getDoctors(Integer page, Integer size, String sortBy, String order, String firstName, String lastName, String specializationId, String cityId);
    Void reVerifyDoctor(ReVerifyDoctorRequest req);
}
