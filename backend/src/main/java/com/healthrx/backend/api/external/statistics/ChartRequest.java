package com.healthrx.backend.api.external.statistics;

import com.healthrx.backend.api.internal.enums.ChartType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartRequest {
    private String dataId;
    private ChartType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
