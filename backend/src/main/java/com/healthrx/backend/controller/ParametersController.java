package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.service.ParametersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
