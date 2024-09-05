package com.healthrx.backend.api.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugPackResponse {
    private Integer id;
    private String gtinCode;
    private String accessibilityCategory;
    private String packType;
    private Integer packSize;
    private String packUnit;
}
