package com.healthrx.backend.service;

import com.healthrx.backend.api.external.PageResponse;
import com.healthrx.backend.api.external.activities.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {
    List<ActivityDTO> getAllActivitiesDTOs();
    ActivitiesResponse getAllActivities();
    ActivityDTO addActivity(ActivityRequest req);
    ActivityDTO editActivity(String id, ActivityRequest req);
    Void deleteActivity(String id);
    PageResponse<UserActivityResponse> getUserActivities(Integer page, Integer size, String sortBy, String order, String activityId, LocalDateTime startDate, LocalDateTime endDate);
    UserActivityResponse addUserActivity(UserActivityRequest request);
    UserActivityResponse editUserActivity(UserActivityRequest request, String id);
    Void deleteUserActivity(String id);
}
