package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.ReVerifyDoctorRequest;
import com.healthrx.backend.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@Tag(name = "Doctor controller", description = "Controller for managing doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping()
    @Operation(summary = "Fetching the list of verified doctors", description = "Fetching a list of doctors with the option to filter")
    public ResponseEntity<PageResponse<DoctorResponse>> getDoctors(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "asc", required = false) String order,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "specialization", required = false) String specializationId,
            @RequestParam(name = "city", required = false) String cityId
    ) {
        return ResponseEntity.ok(doctorService.getDoctors(page, size, sortBy, order, firstName, lastName, specializationId, cityId));
    }

    @PatchMapping("/reVerify")
    @Operation(summary = "Submitting a new application for verification of a doctor's qualifications", description = "Submitting a new application for verification of a doctor's qualifications")
    public ResponseEntity<Void> reVerifyDoctor(@RequestBody ReVerifyDoctorRequest req) {
        return ResponseEntity.ok(doctorService.reVerifyDoctor(req));
    }
}
