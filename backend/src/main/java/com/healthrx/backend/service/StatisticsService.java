package com.healthrx.backend.service;

import com.healthrx.backend.api.external.statistics.ChartRequest;
import com.healthrx.backend.api.external.statistics.ChartResponse;
import com.healthrx.backend.api.external.statistics.ParameterStatisticsResponse;
import com.healthrx.backend.api.external.statistics.StatisticsRequest;
import com.healthrx.backend.api.internal.enums.ChartType;

import java.util.List;

public interface StatisticsService {
    ChartResponse getChartData(ChartRequest request, ChartType type);
    List<ParameterStatisticsResponse> getParametersStatistics(StatisticsRequest req);
}
