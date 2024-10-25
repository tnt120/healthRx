package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.DoctorResponse;
import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.external.UserResponse;
import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.external.activities.ActivityRequest;
import com.healthrx.backend.api.external.admin.AdminParameterResponse;
import com.healthrx.backend.api.external.admin.ChangeRoleReqRes;
import com.healthrx.backend.api.external.admin.DeleteUserRequest;
import com.healthrx.backend.api.external.admin.DoctorVerificationRequest;
import com.healthrx.backend.api.external.paramters.ParameterRequest;
import com.healthrx.backend.api.internal.enums.Role;
import com.healthrx.backend.service.ActivityService;
import com.healthrx.backend.service.AdminService;
import com.healthrx.backend.service.ParametersService;
import com.healthrx.backend.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin controller", description = "Controller for managing admin actions")
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final ParametersService parametersService;
    private final ActivityService activityService;

    @GetMapping("/approvals")
    public ResponseEntity<PageResponse<DoctorResponse>> getApprovals(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size
    ) {
        return ResponseEntity.ok(adminService.getApprovals(page, size));
    }

    @PatchMapping("/verifyDoctor")
    public ResponseEntity<Void> verifyDoctor(@RequestBody DoctorVerificationRequest req) {
        return ResponseEntity.ok(adminService.verifyDoctor(req));
    }

    @GetMapping("/parameters")
    public ResponseEntity<AdminParameterResponse> getParameters() {
        return ResponseEntity.ok(parametersService.getAllParameters());
    }

    @PostMapping("/parameters")
    public ResponseEntity<ParameterDTO> addParameter(@RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.addParameter(req));
    }

    @PatchMapping("/parameters/{id}")
    public ResponseEntity<ParameterDTO> editParameter(@PathVariable String id, @RequestBody ParameterRequest req) {
        return ResponseEntity.ok(parametersService.editParameter(id, req));
    }

    @DeleteMapping("/parameters/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable String id) {
        parametersService.deleteParameter(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activities")
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivitiesDTOs());
    }

    @PostMapping("/activities")
    public ResponseEntity<ActivityDTO> addActivity(@RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.addActivity(req));
    }

    @PatchMapping("/activities/{id}")
    public ResponseEntity<ActivityDTO> editActivity(@PathVariable String id, @RequestBody ActivityRequest req) {
        return ResponseEntity.ok(activityService.editActivity(id, req));
    }

    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id) {
        return ResponseEntity.ok(activityService.deleteActivity(id));
    }

    @GetMapping("/users")
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
    public ResponseEntity<ChangeRoleReqRes> changeUserRole(@PathVariable String id, @RequestBody ChangeRoleReqRes req) {
        return ResponseEntity.ok(userService.changeRole(id, req));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, @RequestParam(name = "message") String message) {
        return ResponseEntity.ok(userService.deleteUser(id, message));
    }
}
