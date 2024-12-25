package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.service.ParametersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
@Tag(name = "Parameters controller", description = "Controller for managing monitored user parameters")
public class ParametersController {

    private final ParametersService parametersService;

    @GetMapping("/users")
    @Operation(summary = "Fetching parameters monitored by users", description = "Fetching parameters monitored by users")
    public ResponseEntity<List<UserParametersResponse>> getUserParameters() {
        return ResponseEntity.ok(parametersService.getUserParameters());
    }

    @PatchMapping("/users")
    @Operation(summary = "Editing parameters monitored by users", description = "Editing parameters monitored by users")
    public ResponseEntity<List<UserParametersResponse>> editUserParameters(@RequestBody List<ParameterDTO> request) {
        return ResponseEntity.ok(parametersService.editUserParameters(request));
    }

    @PostMapping("/monitor")
    @Operation(summary = "Recording the measurement value of the monitored parameter", description = "Recording of parameters monitored by users")
    public ResponseEntity<List<UserParametersResponse>> setMonitorUserParameters(@RequestBody List<UserParametersRequest> request) {
        return ResponseEntity.ok(parametersService.setMonitorUserParameters(request));
    }

    @PatchMapping("/monitor")
    @Operation(summary = "Editing the measurement value of the monitored parameter", description = "Editing parameters monitored by users")
    public ResponseEntity<UserParametersResponse> editMonitorUserParameters(@RequestBody UserParametersRequest request) {
        return ResponseEntity.ok(parametersService.editMonitorUserParameters(request));
    }
}
