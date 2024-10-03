package com.healthrx.backend.api.external.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponse {
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ChartDataDTO> data;
}