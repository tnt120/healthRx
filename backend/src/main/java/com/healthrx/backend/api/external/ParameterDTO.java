package com.healthrx.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDTO {
    private String id;
    private String name;
    private String unit;
    private String minValue;
    private String maxValue;
}
