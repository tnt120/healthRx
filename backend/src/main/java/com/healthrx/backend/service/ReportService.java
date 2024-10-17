package com.healthrx.backend.service;

import com.healthrx.backend.api.external.raport.GenerateReportRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
    void generateReport(GenerateReportRequest req, HttpServletResponse response);
}
