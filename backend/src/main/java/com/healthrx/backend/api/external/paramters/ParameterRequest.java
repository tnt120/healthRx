package com.healthrx.backend.api.external.paramters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterRequest {
    private String unitId;
    private String hint;
    private Double maxValue;
    private Double minValue;
    private Double maxStandardValue;
    private Double minStandardValue;
    private String name;
}
