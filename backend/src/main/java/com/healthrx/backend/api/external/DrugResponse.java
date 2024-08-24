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
public class DrugResponse {
    private Integer id;
    private List<String> atcCodes;
    private String name;
    private String power;
    private String pharmaceuticalFormName;
    private String unit;
}
