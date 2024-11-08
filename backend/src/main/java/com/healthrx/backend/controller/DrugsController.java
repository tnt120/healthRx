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
@Tag(name = "Drugs controller", description = "Kontroler do zarządzania lekami")
public class DrugsController {
    private final DrugsService drugsService;

    @GetMapping
    @Operation(summary = "Pobranie listy leków", description = "Pobranie listy leków z możliwością filtrowania")
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
    @Operation(summary = "Pobranie listy leków użytkownika", description = "Pobranie listy leków użytkownika")
    public ResponseEntity<List<DrugResponse>> getDrugsFromUser() {
        return ResponseEntity.ok(drugsService.getDrugsFromUser());
    }

    @GetMapping("/packs/{id}")
    @Operation(summary = "Pobranie opakowań leku", description = "Pobranie opakowań leku")
    public ResponseEntity<DrugPacksResponse> getDrugPacks(@PathVariable Integer id) {
        return ResponseEntity.ok(drugsService.getDrugPacks(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Pobranie apteczki leków użytkownika", description = "Pobranie apteczki leków użytkownika z możliwością paginacji")
    public ResponseEntity<PageResponse<UserDrugsResponse>> getUserDrugs(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "desc", required = false) String order
    ) {
        return ResponseEntity.ok(drugsService.getUserDrugs(page, size, sortBy, order));
    }

    @PostMapping("/user")
    @Operation(summary = "Dodanie leku do apteczki użytkownika", description = "Dodanie leku do apteczki użytkownika")
    public ResponseEntity<UserDrugsResponse> addUserDrug(@RequestBody UserDrugsRequest userDrugsResponse) {
        return ResponseEntity.ok(drugsService.addUserDrug(userDrugsResponse));
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Edycja leku w apteczce użytkownika", description = "Edycja leku w apteczce użytkownika")
    public ResponseEntity<UserDrugsResponse> updateUserDrug(@RequestBody UserDrugsRequest userDrugsResponse, @PathVariable String id) {
        return ResponseEntity.ok(drugsService.editUserDrug(userDrugsResponse, id));
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "Usunięcie leku z apteczki użytkownika", description = "Usunięcie leku z apteczki użytkownika")
    public ResponseEntity<Void> deleteUserDrug(@PathVariable String id) {
        return ResponseEntity.ok(drugsService.deleteUserDrug(id));
    }

    @GetMapping("/monitor")
    @Operation(summary = "Pobranie listy leków użytkownika z apteczki do zażycia w danym dniu", description = "Pobranie listy leków użytkownika z apteczki do zażycia w danym dniu")
    public ResponseEntity<List<UserDrugMonitorResponse>> getUserDrugMonitor() {
        return ResponseEntity.ok(drugsService.getUserDrugMonitor());
    }

    @PostMapping("/monitor")
    @Operation(summary = "Odznaczenie przyjęcia leku", description = "Odznaczenie przyjęcia leku, z dokładną godziną zażycia")
    public ResponseEntity<UserDrugMonitorResponse> setMonitorDrug(@RequestBody UserDrugMonitorRequest request) {
        return ResponseEntity.ok(drugsService.setUserDrugMonitor(request));
    }

    @PatchMapping("/monitor")
    @Operation(summary = "Edycja godziny zażycia leku", description = "Edycja godziny zażycia leku")
    public ResponseEntity<UserDrugMonitorResponse> editMonitorDrug(@RequestBody UserDrugMonitorRequest request) {
        return ResponseEntity.ok(drugsService.editUserDrugMonitor(request));
    }

    @DeleteMapping("/monitor")
    @Operation(summary = "Usunięcie odznaczenia przyjęcia leku", description = "Usunięcie godziny zażycia leku")
    public ResponseEntity<Void> deleteMonitorDrug(@RequestParam Integer drugId, @RequestParam String time) {
        return ResponseEntity.ok(drugsService.deleteUserDrugMonitor(drugId, time));
    }
}
