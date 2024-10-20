package com.healthrx.backend.service;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.external.paramters.ParameterRequest;

import java.util.List;

public interface ParametersService {
    ParameterDTO addParameter(ParameterRequest request);
    List<ParameterDTO> getAllParameters();
    List<UserParametersResponse> getUserParameters();
    List<UserParametersResponse> fetchUserParameters(String userId);
    List<UserParametersResponse> editUserParameters(List<ParameterDTO> request);
    List<UserParametersResponse> setMonitorUserParameters(List<UserParametersRequest> request);
    UserParametersResponse editMonitorUserParameters(UserParametersRequest request);
}
