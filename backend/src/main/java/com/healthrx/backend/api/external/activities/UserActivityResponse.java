package com.healthrx.backend.api.external.activities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {
    private String id;
    private ActivityDTO activity;
    private LocalDateTime activityTime;
    private Integer duration;
    @JsonInclude
    private Integer averageHeartRate;
    @JsonInclude
    private Integer caloriesBurned;
}
