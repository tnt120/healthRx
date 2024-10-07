package com.healthrx.backend.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrugDayStatistics {
    private Integer totalDaysTaken;
    private Integer totalDaysMissed;
    private Integer totalDaysPartiallyTaken;
}
