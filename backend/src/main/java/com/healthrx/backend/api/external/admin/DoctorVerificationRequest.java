package com.healthrx.backend.api.external.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVerificationRequest {
    private Boolean validVerification;
    private String doctorId;
    private String message;
}
