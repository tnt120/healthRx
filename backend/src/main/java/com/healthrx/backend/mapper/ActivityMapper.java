package com.healthrx.backend.mapper;

import com.healthrx.backend.api.external.activities.ActivityDTO;
import com.healthrx.backend.api.internal.Activity;
import org.springframework.stereotype.Component;

@Component
public class ActivityMapper {

    public ActivityDTO map(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .name(activity.getName())
                .metFactor(activity.getMetFactor())
                .build();
    }
}
