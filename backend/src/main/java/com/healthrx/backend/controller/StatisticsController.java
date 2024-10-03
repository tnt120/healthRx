package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.statistics.ChartRequest;
import com.healthrx.backend.api.external.statistics.ChartResponse;
import com.healthrx.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/chart")
    public ResponseEntity<ChartResponse> getChartData (@RequestBody ChartRequest request) {
        return ResponseEntity.ok(statisticsService.getChartData(request, request.getType()));
    }
}
