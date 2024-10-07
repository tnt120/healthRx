package com.healthrx.backend.api.external.statistics;

import com.healthrx.backend.api.external.DrugResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugStatisticsResponse {
    private DrugResponse drug;
    private Integer totalDosesTaken;
    private Integer totalDosesMissed;
    private Integer totalDaysTaken;
    private Integer totalDaysMissed;
    private Integer totalDaysPartiallyTaken;
    private Double compliancePercentage;
    private Double punctualityPercentage;
    private Double avgDelay;
    private LocalDate firstLogDate;
    private LocalDate lastLogDate;
}
