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
@Tag(name = "Admin controller", description = "Controller for data management by the administrator")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final ParametersService parametersService;
    private final ActivityService activityService;

    @GetMapping("/dashboard")
    @Operation(summary = "Fetching system data to the administrator's dashboard", description = "Fetching data to the dashboard")
    public ResponseEntity<DashboardDataResponse> getDashboardData() {
        return ResponseEntity.ok(adminService.getDashboardData());
    }

    @GetMapping("/approvals")
    @Operation(summary = "Fetching doctor verification applications", description = "Fetching data for approval - pagination option")
    public ResponseEntity<PageResponse<DoctorResponse>> getApprovals(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size
    ) {
        return ResponseEntity.ok(adminService.getApprovals(page, size));
    }

    @PatchMapping("/verifyDoctor")
    @Operation(summary = "Approval of Physician Verification Request", description = "Approval of Physician Verification Request")
    public ResponseEntity<Void> verifyDoctor(@RequestBody DoctorVerificationRequest req) {
        return ResponseEntity.ok(adminService.verifyDoctor(req));
    }

    @GetMapping("/parameters")
    @Operation(summary = "Fetching parameters available in the system", description = "Fetching system parameters")
    public ResponseEntity<AdminParameterResponse> getParameters() {
        return ResponseEntity.ok(parametersService.getAllParameters());
    }

    @PostMapping("/parameters")
    @Operation(summary = "Adding a parameter to the system", description = "Adding a parameter")
    public ResponseEntity<ParameterDTO> addParameter(@RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.addParameter(req));
    }

    @PatchMapping("/parameters/{id}")
    @Operation(summary = "Editing a parameter to the system", description = "Edit parameter")
    public ResponseEntity<ParameterDTO> editParameter(@PathVariable String id, @RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.editParameter(id, req));
    }

    @DeleteMapping("/parameters/{id}")
    @Operation(summary = "Removing a parameter from the system", description = "Parameter removal")
    public ResponseEntity<Void> deleteParameter(@PathVariable String id) {
        parametersService.deleteParameter(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activities")
    @Operation(summary = "Fetching activities available in the system", description = "Fetching activities")
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivitiesDTOs());
    }

    @PostMapping("/activities")
    @Operation(summary = "Adding activity to the system", description = "Adding activity")
    public ResponseEntity<ActivityDTO> addActivity(@RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.addActivity(req));
    }

    @PatchMapping("/activities/{id}")
    @Operation(summary = "Editing activity in the system", description = "Edit activity")
    public ResponseEntity<ActivityDTO> editActivity(@PathVariable String id, @RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.editActivity(id, req));
    }

    @DeleteMapping("/activities/{id}")
    @Operation(summary = "Removing activity from the system", description = "Deleting activity")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
    }

    @GetMapping("/users")
    @Operation(summary = "Fetching users", description = "Fetching users - ability to filter by role, name and surname and pagination")
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
    @Operation(summary = "Changing user role", description = "Changing user role")
    public ResponseEntity<ChangeRoleReqRes> changeUserRole(@PathVariable String id, @RequestBody ChangeRoleReqRes req) {
        return ResponseEntity.ok(userService.changeRole(id, req));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "User deletion", description = "User deletion")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @RequestParam(name = "message") String message) {
        return ResponseEntity.ok(userService.deleteUser(id, message));
    }
}
