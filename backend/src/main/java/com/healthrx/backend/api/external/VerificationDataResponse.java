package com.healthrx.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDataResponse {
    private String userEmail;
    private String role;
    private List<ParameterDTO> parameters;
    private List<SpecializationDTO> specializations;
    private List<CityDTO> cities;
}
