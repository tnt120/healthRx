package com.healthrx.backend.service;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.api.internal.enums.ChartType;

import java.util.List;

public interface StatisticsService {
    ChartResponse getChartData(ChartRequest request, ChartType type);
    List<ParameterStatisticsResponse> getParametersStatistics(StatisticsRequest req);
    List<DrugStatisticsResponse> getDrugsStatistics(StatisticsRequest req);
    List<ActivityStatisticsResponse> getActivitiesStatistics(StatisticsRequest req);
}
