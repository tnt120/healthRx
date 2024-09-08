package com.healthrx.backend.api.external;

import com.healthrx.backend.api.internal.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private LocalDate birthDate;
    private String phoneNumber;

    private List<ParameterDTO> parameters;
    private Double height;
    private LocalTime parametersNotifications;
    private Boolean isBadResultsNotificationsEnabled;
    private Boolean isDrugNotificationsEnabled;

    private List<SpecializationDTO> specializations;
    private String numberPWZ;
    private String numberPESEL;
    private String idPhotoFrontUrl;
    private String idPhotoBackUrl;
    private String profilePictureUrl;
    private CityDTO city;
}
