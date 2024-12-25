package com.healthrx.backend.controller;

import com.healthrx.backend.api.external.raport.GenerateReportRequest;
import com.healthrx.backend.api.external.statistics.*;
import com.healthrx.backend.service.ReportService;
import com.healthrx.backend.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics controller", description = "Controller for managing statistics and generating reports")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final ReportService reportService;

    @PostMapping("/chart")
    @Operation(summary = "Fetching data to a chart for parameters, drugs or activities", description = "Fetching data to a chart for parameters, drugs or activities")
    public ResponseEntity<ChartResponse> getChartData (@RequestBody ChartRequest request) {
        return ResponseEntity.ok(statisticsService.getChartData(request, request.getType()));
    }

    @PostMapping("/parameters")
    @Operation(summary = "Fetching statistics for parameters", description = "Fetching statistics for parameters")
    public ResponseEntity<List<ParameterStatisticsResponse>> getParametersStatistics (@RequestBody StatisticsRequest request) {
        return ResponseEntity.ok(statisticsService.getParametersStatistics(request));
    }

    @PostMapping("/drugs")
    @Operation(summary = "Fetching statistics for drugs", description = "Fetching statistics for drugs")
    public ResponseEntity<List<DrugStatisticsResponse>> getDrugsStatistics (@RequestBody StatisticsRequest request) {
        return ResponseEntity.ok(statisticsService.getDrugsStatistics(request));
    }

    @PostMapping("/activities")
    @Operation(summary = "Fetching statistics for activities", description = "Fetching statistics for activities")
    public ResponseEntity<List<ActivityStatisticsResponse>> getActivitiesStatistics (@RequestBody StatisticsRequest request) {
        return ResponseEntity.ok(statisticsService.getActivitiesStatistics(request));
    }

    @PostMapping("/generate-report")
    @Operation(summary = "Generating a health report", description = "Generating a health report")
    public ResponseEntity<Void> generateReport(@RequestBody GenerateReportRequest req, HttpServletResponse response) {
        reportService.generateReport(req, response);
        return ResponseEntity.ok().build();
    }
}
