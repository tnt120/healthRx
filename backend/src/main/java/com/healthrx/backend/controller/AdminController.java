package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.external.activities.ActivityRequest;
import com.healthrx.backend.api.external.admin.AdminParameterResponse;
import com.healthrx.backend.api.external.admin.ChangeRoleReqRes;
import com.healthrx.backend.api.external.admin.DashboardDataResponse;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.ParametersService;
import com.healthrx.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin controller", description = "Kontroler do zarządzania danymi przez administratora")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final ParametersService parametersService;
    private final ActivityService activityService;

    @GetMapping("/dashboard")
    @Operation(summary = "Pobranie danych systemu do dashboardu administratora", description = "Pobranie danych do dashboardu")
    public ResponseEntity<DashboardDataResponse> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    @GetMapping("/approvals")
    @Operation(summary = "Pobranie wniosków weryfikacyjnych lekarzy", description = "Pobranie danych do zatwierdzenia - możliwość paginacji")
    public ResponseEntity<PageResponse<DoctorResponse>> getApprovals(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size
    ) {
        return ResponseEntity.ok(adminService.getApprovals(page, size));
    }

    @PatchMapping("/verifyDoctor")
    @Operation(summary = "Zatwierdzenie wniosku weryfikacyjnego lekarza", description = "Zatwierdzenie wniosku weryfikacyjnego lekarza")
    public ResponseEntity<Void> verifyDoctor(@RequestBody DoctorVerificationRequest req) {
        return ResponseEntity.ok(adminService.verifyDoctor(req));
    }

    @GetMapping("/parameters")
    @Operation(summary = "Pobranie parametrów dostępnych w systemie", description = "Pobranie parametrów systemu")
    public ResponseEntity<AdminParameterResponse> getParameters() {
        return ResponseEntity.ok(parametersService.getAllParameters());
    }

    @PostMapping("/parameters")
    @Operation(summary = "Dodanie parametru do systemu", description = "Dodanie parametru")
    public ResponseEntity<ParameterDTO> addParameter(@RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.addParameter(req));
    }

    @PatchMapping("/parameters/{id}")
    @Operation(summary = "Edycja parametru do systemu", description = "Edycja parametru")
    public ResponseEntity<ParameterDTO> editParameter(@PathVariable String id, @RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.editParameter(id, req));
    }

    @DeleteMapping("/parameters/{id}")
    @Operation(summary = "Usunięcie parametru z systemi", description = "Usunięcie parametru")
    public ResponseEntity<Void> deleteParameter(@PathVariable String id) {
        parametersService.deleteParameter(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activities")
    @Operation(summary = "Pobranie aktywności dostępnych w systemie", description = "Pobranie aktywności")
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivitiesDTOs());
    }

    @PostMapping("/activities")
    @Operation(summary = "Dodanie aktywności do systemu", description = "Dodanie aktywności")
    public ResponseEntity<ActivityDTO> addActivity(@RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.addActivity(req));
    }

    @PatchMapping("/activities/{id}")
    @Operation(summary = "Edycja aktywności w systemie", description = "Edycja aktywności")
    public ResponseEntity<ActivityDTO> editActivity(@PathVariable String id, @RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.editActivity(id, req));
    }

    @DeleteMapping("/activities/{id}")
    @Operation(summary = "Usunięcie aktywności z systemu", description = "Usunięcie aktywności")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
    }

    @GetMapping("/users")
    @Operation(summary = "Pobranie użytkowników", description = "Pobranie użytkowników - możliwość filtrowania po roli, imieniu i nazwisku oraz paginacja")
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "role", required = false) Role role,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName
    ) {
        return ResponseEntity.ok(userService.getUsers(page, size, role, firstName, lastName));
    }

    @PatchMapping("/users/{id}")
    @Operation(summary = "Zmiana roli użytkownika", description = "Zmiana roli użytkownika")
    public ResponseEntity<ChangeRoleReqRes> changeUserRole(@PathVariable String id, @RequestBody ChangeRoleReqRes req) {
        return ResponseEntity.ok(userService.changeRole(id, req));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Usunięcie użytkownika", description = "Usunięcie użytkownika")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @RequestParam(name = "message") String message) {
        return ResponseEntity.ok(userService.deleteUser(id, message));
    }
}
