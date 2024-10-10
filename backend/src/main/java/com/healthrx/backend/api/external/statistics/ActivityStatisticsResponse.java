package com.healthrx.backend.api.external.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthrx.backend.api.external.activities.ActivityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatisticsResponse {
    private ActivityDTO activity;
    private Integer minDuration;
    private Integer maxDuration;
    private Double avgDuration;
    @JsonInclude
    private Integer minHeartRate;
    @JsonInclude
    private Integer maxHeartRate;
    @JsonInclude
    private Double avgHeartRate;
    @JsonInclude
    private Integer minCaloriesBurned;
    @JsonInclude
    private Integer maxCaloriesBurned;
    @JsonInclude
    private Double avgCaloriesBurned;
    private Integer logsCount;
    private Double hoursCount;
    @JsonInclude
    private LocalDateTime firstLogDate;
    @JsonInclude
    private LocalDateTime lastLogDate;
}
