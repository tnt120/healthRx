package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.ActivityDTO;
import com.healthrx.backend.api.internal.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDTO map(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .caloriesPerHour(activity.getCaloriesPerHour())
                .build();
    }
}
