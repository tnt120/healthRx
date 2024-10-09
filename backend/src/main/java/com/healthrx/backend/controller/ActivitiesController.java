package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.UserActivityRequest;
import com.healthrx.backend.api.external.activities.UserActivityResponse;
import com.healthrx.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivitiesController {
    private final ActivityService activityService;

    @GetMapping("/user")
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
    public ResponseEntity<UserActivityResponse> addUserActivity(@RequestBody UserActivityRequest request) {
        return ResponseEntity.ok(activityService.addUserActivity(request));
    }
}
