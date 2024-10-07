package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/chart")
    public ResponseEntity<ChartResponse> getChartData (@RequestBody ChartRequest request) {
        return ResponseEntity.ok(statisticsService.getChartData(request, request.getType()));
    }

    @PostMapping("/parameters")
    public ResponseEntity<List<ParameterStatisticsResponse>> getParametersStatistics (@RequestBody StatisticsRequest request) {
        return ResponseEntity.ok(statisticsService.getParametersStatistics(request));
    }

    @PostMapping("/drugs")
    public ResponseEntity<List<DrugStatisticsResponse>> getDrugsStatistics (@RequestBody StatisticsRequest request) {
        return ResponseEntity.ok(statisticsService.getDrugsStatistics(request));
    }
}
