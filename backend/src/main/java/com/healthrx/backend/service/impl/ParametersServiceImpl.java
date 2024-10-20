package com.healthrx.backend.service.impl;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.api.internal.*;
import com.healthrx.backend.mapper.ParameterMapper;
import com.healthrx.backend.mapper.UserParameterMapper;
import com.healthrx.backend.repository.ParameterLogRepository;
import com.healthrx.backend.repository.ParameterRepository;
import com.healthrx.backend.repository.UnitRepository;
import com.healthrx.backend.repository.UserParameterRepository;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.ParametersService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.healthrx.backend.handler.BusinessErrorCodes.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParametersServiceImpl implements ParametersService {

    private final ParameterRepository parameterRepository;
    private final UserParameterRepository userParameterRepository;
    private final ParameterLogRepository parameterLogRepository;
    private final AdminService adminService;
    private final UnitRepository unitRepository;
    private final ParameterMapper parameterMapper;
    private final UserParameterMapper userParameterMapper;
    private final Supplier<User> principalSupplier;

    @Override
    public ParameterDTO addParameter(ParameterRequest req) {
        adminService.checkPermissions();

        Unit unit = unitRepository.findById(req.getUnitId())
                .orElseThrow(UNIT_NOT_FOUND::getError);

        parameterRepository.findByName(req.getName()).ifPresent(parameter -> {
            throw PARAMETER_ALREADY_EXISTS.getError();
        });

        return parameterMapper.map(parameterRepository.save(Parameter.builder()
                .hint(req.getHint())
                .maxValue(req.getMaxValue())
                .minValue(req.getMinValue())
                .maxStandardValue(req.getMaxStandardValue())
                .minStandardValue(req.getMinStandardValue())
                .name(req.getName())
                .unit(unit)
                .build()));
    }

    @Override
    @Transactional
    public List<UserParametersResponse> getUserParameters() {
        User user = principalSupplier.get();

        return fetchUserParameters(user.getId());
    }

    @Override
    public List<UserParametersResponse> fetchUserParameters(String userId) {
        return userParameterRepository.findAllByUserId(userId)
                .stream()
                .map(userParameter -> {
                    Double parameterValue = parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(
                            userParameter.getParameter().getId(),
                            userId
                    ).orElse(null);

                    return userParameterMapper.map(userParameter, parameterValue);
                })
                .toList();
    }

    @Override
    @Transactional
    public List<UserParametersResponse> editUserParameters(List<ParameterDTO> request) {

        User user = principalSupplier.get();

        List<UserParameter> existingUserParameters = this.userParameterRepository.findAllByUserId(user.getId());
        Map<String, UserParameter> existingUserParametersMap = existingUserParameters.stream()
                .collect(Collectors.toMap(up -> up.getParameter().getId(), up -> up));

        List<UserParametersResponse> response = new ArrayList<>();

        request.forEach(parameterDTO -> {
            Parameter parameter = this.parameterRepository.findById(parameterDTO.getId())
                    .orElseThrow(PARAMETER_NOT_FOUND::getError);

            String userParameterId;

            if (!existingUserParametersMap.containsKey(parameterDTO.getId())) {
                userParameterId = this.userParameterRepository.save(UserParameter.builder()
                        .parameter(parameter)
                        .user(user)
                        .build()).getId();
            } else {
                userParameterId = existingUserParametersMap.get(parameterDTO.getId()).getId();
                existingUserParametersMap.remove(parameterDTO.getId());
            }

            Double parameterValue = parameterLogRepository.findParameterLogValueByParameterIdAndUserIdAndToday(
                    parameter.getId(),
                    user.getId()
            ).orElse(null);

            response.add(userParameterMapper.map(UserParameter.builder()
                    .id(userParameterId)
                    .parameter(parameter)
                    .user(user)
                    .build(), parameterValue));
        });

        userParameterRepository.deleteAll(existingUserParametersMap.values());

        return response;
    }

    @Override
    @Transactional
    public List<UserParametersResponse> setMonitorUserParameters(List<UserParametersRequest> request) {

        User user = principalSupplier.get();

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
                    .build()), userParameterReq.getId()));
        });

        return response;
    }

    @Override
    public UserParametersResponse editMonitorUserParameters(UserParametersRequest request) {

        User user = principalSupplier.get();

        ParameterLog parameterLog = this.parameterLogRepository.findParameterLogByParameterIdAndUserIdAndToday(
                request.getParameterId(),
                user.getId()
        ).orElseThrow(PARAMETER_LOG_NOT_FOUND::getError);

        parameterLog.setValue(request.getValue());

        return this.parameterMapper.map(this.parameterLogRepository.save(parameterLog), request.getId());
    }
}
