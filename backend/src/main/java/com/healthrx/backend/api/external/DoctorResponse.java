package com.healthrx.backend.api.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private String id;
    private String firstName;
    private String lastName;
    private List<SpecializationDTO> specializations;
    private CityDTO city;
    private String numberPWZ;
    @JsonInclude
    private byte[] pictureUrl;
    private byte[] frontPwz;
    private byte[] backPwz;
}
