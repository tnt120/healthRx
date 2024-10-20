package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.ParameterLog;
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
                .maxStandardValue(parameter.getMaxStandardValue())
                .minStandardValue(parameter.getMinStandardValue())
                .hint(parameter.getHint())
                .build();
    }

    public UserParametersResponse map(ParameterLog parameterLog, String userParameterId) {
        return UserParametersResponse.builder()
                .id(userParameterId)
                .parameter(map(parameterLog.getParameter()))
                .value(parameterLog.getValue())
                .build();
    }
}
