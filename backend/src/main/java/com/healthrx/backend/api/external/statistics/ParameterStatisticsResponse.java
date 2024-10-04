package com.healthrx.backend.api.external.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthrx.backend.api.external.ParameterDTO;
import com.healthrx.backend.api.internal.enums.TrendType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterStatisticsResponse {
    private ParameterDTO parameter;
    private Double avgValue;
    private Double minValue;
    private Double maxValue;
    private Integer missedDays;
    private Integer longestBreak;
    @JsonInclude
    private LocalDateTime firstLogDate;
    @JsonInclude
    private LocalDateTime lastLogDate;
    @JsonInclude
    private TrendType trend;
}
