package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.external.activities.UserActivityResponse;
import com.healthrx.backend.api.internal.Activity;
import com.healthrx.backend.api.internal.ActivityLog;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDTO map(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .metFactor(activity.getMetFactor())
                .isPopular(activity.getIsPopular())
                .build();
    }

    public UserActivityResponse map(ActivityLog activityLog) {
        return UserActivityResponse.builder()
                .id(activityLog.getId())
                .activity(map(activityLog.getActivity()))
                .activityTime(activityLog.getActivityTime())
                .duration(activityLog.getDuration())
                .averageHeartRate(activityLog.getAverageHeartRate())
                .caloriesBurned(activityLog.getCaloriesBurned())
                .build();
    }
}
