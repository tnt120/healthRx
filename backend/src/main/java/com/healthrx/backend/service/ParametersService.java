package com.healthrx.backend.service;

import com.healthrx.backend.api.external.ParameterDTO;

import java.util.List;

public interface ParametersService {
    List<ParameterDTO> editUserParameters(List<ParameterDTO> request);
}
