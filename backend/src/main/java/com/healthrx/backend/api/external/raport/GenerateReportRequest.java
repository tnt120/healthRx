package com.healthrx.backend.api.external.raport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String userId;
    private Boolean userDrugs;
    private Boolean parametersStats;
    private Boolean drugsStats;
    private Boolean activitiesStats;
}
