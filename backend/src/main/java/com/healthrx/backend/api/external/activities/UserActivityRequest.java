package com.healthrx.backend.api.external.activities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityRequest {
    private String activityId;
    private Integer duration;
    private LocalDateTime activityTime;
    private Integer averageHeartRate;
    private Integer caloriesBurned;
}
