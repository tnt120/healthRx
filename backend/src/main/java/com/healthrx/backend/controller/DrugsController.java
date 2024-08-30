package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.service.DrugsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user")
    public ResponseEntity<PageResponse<UserDrugsResponse>> getUserDrugs(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "desc", required = false) String order
    ) {
        return ResponseEntity.ok(drugsService.getUserDrugs(page, size, sortBy, order));
    }

    @PostMapping("/user")
    public ResponseEntity<UserDrugsResponse> addUserDrug(@RequestBody UserDrugsRequest userDrugsResponse) {
        return ResponseEntity.ok(drugsService.addUserDrug(userDrugsResponse));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDrugsResponse> updateUserDrug(@RequestBody UserDrugsRequest userDrugsResponse, @PathVariable String id) {
        return ResponseEntity.ok(drugsService.editUserDrug(userDrugsResponse, id));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUserDrug(@PathVariable String id) {
        return ResponseEntity.ok(drugsService.deleteUserDrug(id));
    }

    @GetMapping("/monitor")
    public ResponseEntity<List<UserDrugMonitorResponse>> getUserDrugMonitor() {
        return ResponseEntity.ok(drugsService.getUserDrugMonitor());
    }

    @PostMapping("/monitor")
    public ResponseEntity<UserDrugMonitorResponse> setMonitorParameter(@RequestBody UserDrugMonitorRequest request) {
        return ResponseEntity.ok(drugsService.setUserDrugMonitor(request));
    }
}
