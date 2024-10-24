package com.healthrx.backend.api.external.admin;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UnitDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminParameterResponse {
    List<ParameterDTO> parameters;
    List<UnitDTO> units;
}
