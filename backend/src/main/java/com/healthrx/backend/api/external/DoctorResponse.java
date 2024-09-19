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
public class DoctorResponse {
    private String firstName;
    private String lastName;
    private List<SpecializationDTO> specializations;
    private CityDTO city;
    private String numberPWZ;
    private String pictureUrl;
}
