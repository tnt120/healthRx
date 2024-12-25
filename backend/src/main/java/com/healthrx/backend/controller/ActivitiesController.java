package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.external.activities.ActivityRequest;
import com.healthrx.backend.api.external.activities.UserActivityRequest;
import com.healthrx.backend.api.external.activities.UserActivityResponse;
import com.healthrx.backend.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Activities controller", description = "Controller for managing user activities")
public class ActivitiesController {
    private final ActivityService activityService;

    @GetMapping("/user")
    @Operation(summary = "Fetching user activity", description = "Fetching user activity - ability to filter by activity, time range and pagination")
    public ResponseEntity<PageResponse<UserActivityResponse>> getUserActivities(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id", required = false) String sortBy,
            @RequestParam(name = "order", defaultValue = "desc", required = false) String order,
            @RequestParam(name = "activityId", required = false) String activityId,
            @RequestParam(name = "startDate", required = false) LocalDateTime startDate,
            @RequestParam(name = "endDate", required = false) LocalDateTime endDate
    ) {
        return ResponseEntity.ok(activityService.getUserActivities(page, size, sortBy, order, activityId, startDate, endDate));
    }

    @PostMapping("/user")
    @Operation(summary = "Adding completed user activity", description = "Adding user activity")
    public ResponseEntity<UserActivityResponse> addUserActivity(@RequestBody UserActivityRequest request) {
        return ResponseEntity.ok(activityService.addUserActivity(request));
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "Editing a user's completed activity", description = "Editing User Activity")
    public ResponseEntity<UserActivityResponse> editUserActivity(@RequestBody UserActivityRequest request, @PathVariable String id) {
        return ResponseEntity.ok(activityService.editUserActivity(request, id));
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "Usunięcie wykonanej aktywności użytkownika", description = "Deleting user activity")
    public ResponseEntity<Void> deleteUserActivity(@PathVariable String id) {
        return ResponseEntity.ok(activityService.deleteUserActivity(id));
    }
}
