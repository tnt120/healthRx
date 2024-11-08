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
@Tag(name = "Parameters controller", description = "Kontroler do zarządzania monitorowanymi parametrami użytkowników")
public class ParametersController {

    private final ParametersService parametersService;

    @GetMapping("/users")
    @Operation(summary = "Pobranie parametrów monitorowanych przez użytkowników", description = "Pobranie parametrów monitorowanych przez użytkowników")
    public ResponseEntity<List<UserParametersResponse>> getUserParameters() {
        return ResponseEntity.ok(parametersService.getUserParameters());
    }

    @PatchMapping("/users")
    @Operation(summary = "Edycja parametrów monitorowanych przez użytkowników", description = "Edycja parametrów monitorowanych przez użytkowników")
    public ResponseEntity<List<UserParametersResponse>> editUserParameters(@RequestBody List<ParameterDTO> request) {
        return ResponseEntity.ok(parametersService.editUserParameters(request));
    }

    @PostMapping("/monitor")
    @Operation(summary = "Zapis wartości pomiaru monitorowanego parametru", description = "Zapis parametrów monitorowanych przez użytkowników")
    public ResponseEntity<List<UserParametersResponse>> setMonitorUserParameters(@RequestBody List<UserParametersRequest> request) {
        return ResponseEntity.ok(parametersService.setMonitorUserParameters(request));
    }

    @PatchMapping("/monitor")
    @Operation(summary = "Edycja wartości pomiaru monitorowanego parametru", description = "Edycja parametrów monitorowanych przez użytkowników")
    public ResponseEntity<UserParametersResponse> editMonitorUserParameters(@RequestBody UserParametersRequest request) {
        return ResponseEntity.ok(parametersService.editMonitorUserParameters(request));
    }
}
