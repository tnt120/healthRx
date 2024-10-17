package com.healthrx.backend.service;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.api.internal.enums.ChartType;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    ChartResponse getChartData(ChartRequest request, ChartType type);
    List<ParameterStatisticsResponse> getParametersStatistics(StatisticsRequest req);
    List<ParameterStatisticsResponse> generateParameterStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<DrugStatisticsResponse> getDrugsStatistics(StatisticsRequest req);
    List<DrugStatisticsResponse> generateDrugStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<ActivityStatisticsResponse> getActivitiesStatistics(StatisticsRequest req);
    List<ActivityStatisticsResponse> generateActivityStats(String userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
