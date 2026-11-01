package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.quality.WaterQualityRequest;
import com.veloia.smartwater.dto.quality.WaterQualityResponse;
import com.veloia.smartwater.dto.quality.WaterQualityStatsDto;
import com.veloia.smartwater.service.WaterQualityService;
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
@RequestMapping("/api/water-quality")
@Tag(name = "Water Quality", description = "Physical-chemical lab samples, pH, turbidity, and residual chlorine monitoring")
public class WaterQualityController {

    private final WaterQualityService qualityService;

    public WaterQualityController(WaterQualityService qualityService) {
        this.qualityService = qualityService;
    }

    @PostMapping
    @Operation(summary = "Record physical-chemical water sample test results")
    public ResponseEntity<ApiResponse<WaterQualityResponse>> recordSample(@Valid @RequestBody WaterQualityRequest request) {
        WaterQualityResponse created = qualityService.recordSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Water quality sample recorded", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated water quality test records with optional location and potability filters")
    public ResponseEntity<ApiResponse<PageResponse<WaterQualityResponse>>> getAllQualityRecords(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String potabilityStatus,
            @PageableDefault(size = 10, sort = "sampleTimestamp", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<WaterQualityResponse> page = qualityService.getAllSamples(locationId, potabilityStatus, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get water quality sample record by ID")
    public ResponseEntity<ApiResponse<WaterQualityResponse>> getQualityRecordById(@PathVariable Long id) {
        WaterQualityResponse record = qualityService.getSampleById(id);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get overall water quality compliance metrics and average chemical levels")
    public ResponseEntity<ApiResponse<WaterQualityStatsDto>> getStatistics() {
        WaterQualityStatsDto stats = qualityService.getQualityStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/trends")
    @Operation(summary = "Get recent chronological water quality parameter trends for Recharts")
    public ResponseEntity<ApiResponse<List<WaterQualityResponse>>> getTrends(
            @RequestParam(defaultValue = "15") int limit) {
        List<WaterQualityResponse> trends = qualityService.getRecentTrends(limit);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
