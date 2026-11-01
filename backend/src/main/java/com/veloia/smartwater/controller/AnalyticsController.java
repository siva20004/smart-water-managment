package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.analytics.AnalyticsSummaryResponse;
import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Executive Analytics", description = "Utility-wide telemetry summaries, active alerts, billing, and quality compliance metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    @Operation(summary = "Get high-level utility analytics and metrics summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getSummary() {
        AnalyticsSummaryResponse summary = analyticsService.getAnalyticsSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export analytics executive report to CSV")
    public ResponseEntity<byte[]> exportAnalyticsCsv(@RequestParam(defaultValue = "executive") String type) {
        AnalyticsSummaryResponse summary = analyticsService.getAnalyticsSummary();

        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value,Unit\n");
        csv.append("Total Customers,").append(summary.getTotalCustomers()).append(",Accounts\n");
        csv.append("Active Water Meters,").append(summary.getTotalActiveMeters()).append(",Instruments\n");
        csv.append("Total Billed Consumption,").append(summary.getTotalConsumptionM3()).append(",m3\n");
        csv.append("Total Billed Revenue,").append(summary.getTotalBilledRevenueJpy()).append(",JPY\n");
        csv.append("Outstanding Balance,").append(summary.getOutstandingBalanceJpy()).append(",JPY\n");
        csv.append("Unpaid Invoices,").append(summary.getUnpaidBillsCount()).append(",Invoices\n");
        csv.append("Open Leak Alerts,").append(summary.getOpenLeakAlertsCount()).append(",Alerts\n");
        csv.append("Critical Leak Alerts,").append(summary.getCriticalAlertsCount()).append(",Alerts\n");
        csv.append("Pending Maintenance Orders,").append(summary.getPendingMaintenanceCount()).append(",Work Orders\n");
        csv.append("Water Quality Compliance Rate,").append(summary.getWaterQualityPotabilityPercent()).append(",%\n");

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"water_utility_analytics_" + System.currentTimeMillis() + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}
