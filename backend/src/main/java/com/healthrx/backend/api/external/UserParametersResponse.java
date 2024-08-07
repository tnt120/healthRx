package com.healthrx.backend.api.external;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserParametersResponse {
    private String id;
    private ParameterDTO parameter;
    @JsonInclude()
    private Double value;
}
