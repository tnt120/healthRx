package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.*;
import com.healthrx.backend.service.DrugsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
@Tag(name = "Drugs controller", description = "Controller for managing medications")
public class DrugsController {
    private final DrugsService drugsService;

    @GetMapping
    @Operation(summary = "Fetching a medication list", description = "Fetching a list of medications with filtering options")
    public ResponseEntity<PageResponse<DrugResponse>> getDrugs(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "asc", required = false) String order,
            @RequestParam(name = "name", required = false) String name
    ) {
        return ResponseEntity.ok(drugsService.getAllDrugs(page, size, sortBy, order, name));
    }

    @GetMapping("/drugsUser")
    @Operation(summary = "Fetching the user's medication list", description = "Fetching the user's medication list")
    public ResponseEntity<List<DrugResponse>> getDrugsFromUser() {
        return ResponseEntity.ok(drugsService.getDrugsFromUser());
    }

    @GetMapping("/packs/{id}")
    @Operation(summary = "Fetching medicine packages", description = "Fetching medicine packages")
    public ResponseEntity<DrugPacksResponse> getDrugPacks(@PathVariable Integer id) {
        return ResponseEntity.ok(drugsService.getDrugPacks(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Fetching the user's medicine kit", description = "Fetching the user's medicine cabinet with the possibility of pagination")
    public ResponseEntity<PageResponse<UserDrugsResponse>> getUserDrugs(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "desc", required = false) String order
    ) {
        return ResponseEntity.ok(drugsService.getUserDrugs(page, size, sortBy, order));
    }

    @PostMapping("/user")
    @Operation(summary = "Adding a medicine to the user's medicine cabinet", description = "Adding a medicine to the user's medicine cabinet")
    public ResponseEntity<UserDrugsResponse> addUserDrug(@RequestBody UserDrugsRequest userDrugsResponse) {
        return ResponseEntity.ok(drugsService.addUserDrug(userDrugsResponse));
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Editing a medicine in the user's medicine cabinet", description = "Editing a medicine in the user's medicine cabinet")
    public ResponseEntity<UserDrugsResponse> updateUserDrug(@RequestBody UserDrugsRequest userDrugsResponse, @PathVariable String id) {
        return ResponseEntity.ok(drugsService.editUserDrug(userDrugsResponse, id));
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "Removing a medicine from the user's medicine cabinet", description = "Removing a medicine from the user's medicine cabinet")
    public ResponseEntity<Void> deleteUserDrug(@PathVariable String id) {
        return ResponseEntity.ok(drugsService.deleteUserDrug(id));
    }

    @GetMapping("/monitor")
    @Operation(summary = "Fetching a list of user's medications from the medicine cabinet to be taken on a given day", description = "Fetching a list of user's medications from the medicine cabinet to be taken on a given day")
    public ResponseEntity<List<UserDrugMonitorResponse>> getUserDrugMonitor() {
        return ResponseEntity.ok(drugsService.getUserDrugMonitor());
    }

    @PostMapping("/monitor")
    @Operation(summary = "Marking the taking of the medicine", description = "Marking the taking of the medicine")
    public ResponseEntity<UserDrugMonitorResponse> setMonitorDrug(@RequestBody UserDrugMonitorRequest request) {
        return ResponseEntity.ok(drugsService.setUserDrugMonitor(request));
    }

    @PatchMapping("/monitor")
    @Operation(summary = "Editing the time of taking the medicine", description = "Editing the time of taking the medicine")
    public ResponseEntity<UserDrugMonitorResponse> editMonitorDrug(@RequestBody UserDrugMonitorRequest request) {
        return ResponseEntity.ok(drugsService.editUserDrugMonitor(request));
    }

    @DeleteMapping("/monitor")
    @Operation(summary = "Unchecking the medicine intake", description = "Removing the time of taking the medicine")
    public ResponseEntity<Void> deleteMonitorDrug(@RequestParam Integer drugId, @RequestParam String time) {
        return ResponseEntity.ok(drugsService.deleteUserDrugMonitor(drugId, time));
    }
}
