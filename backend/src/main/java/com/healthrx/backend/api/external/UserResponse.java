package com.healthrx.backend.api.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthrx.backend.api.internal.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Sex sex;
    private LocalDate birthDate;
    private String phoneNumber;
    private Double height;
    private String role;
    @JsonInclude
    private byte[] pictureUrl;
    private Boolean isDoctorVerified;
    private UnverifiedDoctorDTO unverifiedDoctor;
}
