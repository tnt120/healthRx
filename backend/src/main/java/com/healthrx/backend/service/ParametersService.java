package com.healthrx.backend.service;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;

import java.util.List;

public interface ParametersService {
    List<ParameterDTO> editUserParameters(List<ParameterDTO> request);
    List<UserParametersResponse> setMonitorUserParameters(List<UserParametersRequest> request);
}
