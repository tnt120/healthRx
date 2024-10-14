package com.healthrx.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnverifiedDoctorDTO {
    private String unverifiedMessage;
    private String numberPWZ;
    private String numberPESEL;
}
