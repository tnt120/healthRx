package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.DrugResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.UserDrugsRequest;
import com.healthrx.backend.api.external.UserDrugsResponse;
import com.healthrx.backend.service.DrugsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugsController {
    private final DrugsService drugsService;

    @GetMapping
    public ResponseEntity<PageResponse<DrugResponse>> getDrugs(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "asc", required = false) String order,
            @RequestParam(name = "name", required = false) String name
    ) {
        return ResponseEntity.ok(drugsService.getAllDrugs(page, size, sortBy, order, name));
    }

    @PostMapping("/user")
    public ResponseEntity<UserDrugsResponse> addUserDrug(@RequestBody UserDrugsRequest userDrugsResponse) {
        return ResponseEntity.ok(drugsService.addUserDrug(userDrugsResponse));
    }
}
