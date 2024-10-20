package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserParametersRequest;
import com.healthrx.backend.api.external.UserParametersResponse;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.service.ParametersService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parameters")
@RequiredArgsConstructor
@Tag(name = "Parameters controller", description = "Controller for managing user parameters")
public class ParametersController {

    private final ParametersService parametersService;

    @GetMapping("/admin")
    public ResponseEntity<List<ParameterDTO>> getParameters() {
        return ResponseEntity.ok(parametersService.getAllParameters());
    }

    @PostMapping("/admin")
    public ResponseEntity<ParameterDTO> addParameter(@RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.addParameter(req));
    }

    @PatchMapping("/admin/{id}")
    public ResponseEntity<ParameterDTO> editParameter(@PathVariable String id, @RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.editParameter(id, req));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable String id) {
        parametersService.deleteParameter(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserParametersResponse>> getUserParameters() {
        return ResponseEntity.ok(parametersService.getUserParameters());
    }

    @PatchMapping("/users")
    public ResponseEntity<List<UserParametersResponse>> editUserParameters(@RequestBody List<ParameterDTO> request) {
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
