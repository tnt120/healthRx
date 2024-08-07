package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.internal.UserParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserParameterMapper {

    private final ParameterMapper parameterMapper;

    public UserParametersResponse map(UserParameter userParameter, Double value) {
        return UserParametersResponse.builder()
                .id(userParameter.getId())
                .parameter(parameterMapper.map(userParameter.getParameter()))
                .value(value)
                .build();
    }
}
