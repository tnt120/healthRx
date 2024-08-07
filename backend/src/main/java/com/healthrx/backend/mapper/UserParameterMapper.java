package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.internal.UserParameter;
import com.healthrx.backend.repository.ParameterLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserParameterMapper {

    private final ParameterMapper parameterMapper;
    private final ParameterLogRepository parameterLogRepository;

    public UserParametersResponse map(UserParameter userParameter) {
        return UserParametersResponse.builder()
                .id(userParameter.getId())
                .parameter(parameterMapper.map(userParameter.getParameter()))
                .value(
                        parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(
                                userParameter.getParameter().getId(),
                                userParameter.getUser().getId()
                        ).orElse(null)
                )
                .build();
    }
}
