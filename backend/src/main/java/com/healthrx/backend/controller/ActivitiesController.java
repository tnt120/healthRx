package com.healthrx.backend.controller;

import com.healthrx.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivitiesController {
    private final ActivityService activityService;
}
