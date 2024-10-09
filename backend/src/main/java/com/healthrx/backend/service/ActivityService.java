package com.healthrx.backend.service;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.ActivitiesResponse;
import com.healthrx.backend.api.external.activities.UserActivityResponse;

import java.time.LocalDateTime;

public interface ActivityService {
    ActivitiesResponse getAllActivities();
    PageResponse<UserActivityResponse> getUserActivities(Integer page, Integer size, String sortBy, String order, String activityId, LocalDateTime startDate, LocalDateTime endDate);
}
