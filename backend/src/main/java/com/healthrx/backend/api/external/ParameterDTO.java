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
public class ParameterDTO {
    private String id;
    private String name;
    private String unit;
    private Double minValue;
    private Double maxValue;
    private Double minStandardValue;
    private Double maxStandardValue;
    private String hint;
}
