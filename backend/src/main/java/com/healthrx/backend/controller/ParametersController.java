package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.service.ParametersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
public class ParametersController {

    private final ParametersService parametersService;

    @PatchMapping("/users")
    public ResponseEntity<List<ParameterDTO>> editUserParameters(@RequestBody List<ParameterDTO> request) {
        return ResponseEntity.ok(parametersService.editUserParameters(request));
    }

    @PostMapping("/monitor")
    public ResponseEntity<List<UserParametersResponse>> setMonitorUserParameters(@RequestBody List<UserParametersRequest> request) {
        return ResponseEntity.ok(parametersService.setMonitorUserParameters(request));
    }

    @PatchMapping("/monitor")
    public ResponseEntity<UserParametersResponse> editMonitorUserParameters(@RequestBody UserParametersRequest request) {
        return ResponseEntity.ok(parametersService.editMonitorUserParameters(request));
    }
}
