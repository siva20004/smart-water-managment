package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.analytics.ConsumptionAnalyticsDtos.*;
import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.reading.MeterReadingRequest;
import com.veloia.smartwater.dto.reading.MeterReadingResponse;
import com.veloia.smartwater.service.MeterReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
@Tag(name = "Meter Readings & Consumption", description = "IoT telemetry ingestion, incremental consumption, window functions, and analytics")
public class MeterReadingController {

    private final MeterReadingService readingService;

    public MeterReadingController(MeterReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping
    @Operation(summary = "Submit a water meter reading (calculates consumption against previous valid reading)")
    public ResponseEntity<ApiResponse<MeterReadingResponse>> submitReading(@Valid @RequestBody MeterReadingRequest request) {
        MeterReadingResponse created = readingService.submitReading(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Reading ingested successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated meter readings with optional meter filter")
    public ResponseEntity<ApiResponse<PageResponse<MeterReadingResponse>>> getAllReadings(
            @RequestParam(required = false) Long meterId,
            @PageableDefault(size = 20, sort = "readingTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MeterReadingResponse> page = readingService.getAllReadings(meterId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/meter/{meterId}")
    @Operation(summary = "Get paginated readings for a specific water meter in reverse chronological order")
    public ResponseEntity<ApiResponse<PageResponse<MeterReadingResponse>>> getReadingsByMeter(
            @PathVariable Long meterId,
            @PageableDefault(size = 20, sort = "readingTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MeterReadingResponse> page = readingService.getReadingsByMeter(meterId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/analytics/daily")
    @Operation(summary = "Get daily total water consumption aggregations for trend line chart")
    public ResponseEntity<ApiResponse<List<DailyConsumption>>> getDailyConsumption(
            @RequestParam(defaultValue = "30") int days) {
        List<DailyConsumption> list = readingService.getDailyConsumption(days);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/analytics/monthly")
    @Operation(summary = "Get monthly consumption history for bar chart")
    public ResponseEntity<ApiResponse<List<MonthlyConsumption>>> getMonthlyConsumption() {
        List<MonthlyConsumption> list = readingService.getMonthlyConsumption();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/analytics/customers")
    @Operation(summary = "Get top consuming customers report")
    public ResponseEntity<ApiResponse<List<CustomerConsumption>>> getCustomerConsumption() {
        List<CustomerConsumption> list = readingService.getCustomerConsumption();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/analytics/regions")
    @Operation(summary = "Get consumption distribution by regional sector")
    public ResponseEntity<ApiResponse<List<RegionConsumption>>> getRegionConsumption() {
        List<RegionConsumption> list = readingService.getRegionConsumption();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
