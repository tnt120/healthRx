package com.healthrx.backend.api.external;

import com.healthrx.backend.api.internal.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationRequest {
    private String email;
    private String firstName;
    private String lastName;
    private Sex sex;
    private OffsetDateTime birthDate;
    private String phoneNumber;

    private List<ParameterDTO> parameters;
    private Double height;
    private LocalTime parametersNotifications;
    private boolean isBadResultsNotificationsEnabled;
    private boolean isDrugNotificationsEnabled;

    private List<SpecializationDTO> specializations;
    private String numberPWZ;
    private String numberPESEL;
    private String idPhotoFrontUrl;
    private String idPhotoBackUrl;
    private String profilePictureUrl;
    private CityDTO city;
}
