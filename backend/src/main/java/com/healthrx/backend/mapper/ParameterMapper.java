package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.internal.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ParameterMapper {

    public ParameterDTO map(Parameter parameter) {
        return ParameterDTO.builder()
                .id(parameter.getId())
                .name(parameter.getName())
                .unit(parameter.getUnit().getSymbol())
                .minValue(parameter.getMinValue())
                .maxValue(parameter.getMaxValue())
                .build();
    }
}
