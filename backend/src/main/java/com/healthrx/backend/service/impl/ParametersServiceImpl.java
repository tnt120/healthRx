package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.internal.Parameter;
import com.healthrx.backend.api.internal.ParameterLog;
import com.healthrx.backend.api.internal.User;
import com.healthrx.backend.api.internal.UserParameter;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.repository.ParameterLogRepository;
import com.healthrx.backend.repository.ParameterRepository;
import com.healthrx.backend.repository.UserParameterRepository;
import com.healthrx.backend.service.ParametersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.healthrx.backend.handler.BusinessErrorCodes.PARAMETER_LOG_ALREADY_EXISTS;
import static com.healthrx.backend.handler.BusinessErrorCodes.PARAMETER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametersServiceImpl implements ParametersService {

    private final ParameterRepository parameterRepository;
    private final UserParameterRepository userParameterRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final ParameterMapper parameterMapper;

    @Override
    @Transactional
    public List<ParameterDTO> editUserParameters(List<ParameterDTO> request) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserParameter> existingUserParameters = this.userParameterRepository.findAllByUserId(user.getId());
        Map<String, UserParameter> existingUserParametersMap = existingUserParameters.stream()
                .collect(Collectors.toMap(up -> up.getParameter().getId(), up -> up));


        request.forEach(parameterDTO -> {
            Parameter parameter = this.parameterRepository.findById(parameterDTO.getId())
                    .orElseThrow(PARAMETER_NOT_FOUND::getError);

            if (!existingUserParametersMap.containsKey(parameterDTO.getId())) {
                this.userParameterRepository.save(UserParameter.builder()
                        .parameter(parameter)
                        .user(user)
                        .build());
            } else {
                existingUserParametersMap.remove(parameterDTO.getId());
            }
        });

        userParameterRepository.deleteAll(existingUserParametersMap.values());

        return request;
    }

    @Override
    @Transactional
    public List<UserParametersResponse> setMonitorUserParameters(List<UserParametersRequest> request) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserParametersResponse> response = new ArrayList<>();

        request.forEach(userParameterReq -> {
            Parameter parameter = this.parameterRepository.findById(userParameterReq.getParameterId())
                    .orElseThrow(PARAMETER_NOT_FOUND::getError);

            Double parameterValue = parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(
                    parameter.getId(),
                    user.getId()
            ).orElse(null);

            if (parameterValue != null) {
                throw PARAMETER_LOG_ALREADY_EXISTS.getError();
            }

             response.add(this.parameterMapper.map(this.parameterLogRepository.save(ParameterLog.builder()
                    .parameter(parameter)
                    .user(user)
                    .value(userParameterReq.getValue())
                    .build())));
        });

        return response;
    }
}
