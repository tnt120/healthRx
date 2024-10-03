package com.healthrx.backend.service;

import com.healthrx.backend.api.external.statistics.ChartRequest;
import com.healthrx.backend.api.external.statistics.ChartResponse;
import com.healthrx.backend.api.internal.enums.ChartType;

public interface StatisticsService {
    ChartResponse getChartData(ChartRequest request, ChartType type);
}
