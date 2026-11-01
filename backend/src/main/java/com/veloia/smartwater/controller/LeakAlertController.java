package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.alert.LeakAlertResponse;
import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.service.LeakDetectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leak-alerts")
@Tag(name = "Leak Alerts & Diagnostics", description = "Rule-based acoustic anomaly and flow telemetry leak pattern detection")
public class LeakAlertController {

    private final LeakDetectionService leakService;

    public LeakAlertController(LeakDetectionService leakService) {
        this.leakService = leakService;
    }

    @GetMapping
    @Operation(summary = "Get paginated leak alerts with status, severity, and location filters")
    public ResponseEntity<ApiResponse<PageResponse<LeakAlertResponse>>> getAllAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) Long locationId,
            @PageableDefault(size = 10, sort = "detectedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<LeakAlertResponse> page = leakService.getAllAlerts(status, severity, locationId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get leak alert by ID")
    public ResponseEntity<ApiResponse<LeakAlertResponse>> getAlertById(@PathVariable Long id) {
        LeakAlertResponse alert = leakService.getAlertById(id);
        return ResponseEntity.ok(ApiResponse.success(alert));
    }

    @GetMapping("/code/{alertCode}")
    @Operation(summary = "Get leak alert by unique alert code")
    public ResponseEntity<ApiResponse<LeakAlertResponse>> getAlertByCode(@PathVariable String alertCode) {
        LeakAlertResponse alert = leakService.getAlertByCode(alertCode);
        return ResponseEntity.ok(ApiResponse.success(alert));
    }

    @PostMapping("/scan")
    @Operation(summary = "Run rule-based leak detection algorithm across all active telemetry meters")
    public ResponseEntity<ApiResponse<List<LeakAlertResponse>>> triggerDetectionScan() {
        List<LeakAlertResponse> detected = leakService.runDetectionScan();
        return ResponseEntity.ok(ApiResponse.success("Detection algorithm executed. Found " + detected.size() + " anomaly conditions.", detected));
    }

    @PostMapping("/evaluate/{meterId}")
    @Operation(summary = "Evaluate telemetry rules on a single meter")
    public ResponseEntity<ApiResponse<List<LeakAlertResponse>>> evaluateMeter(@PathVariable Long meterId) {
        List<LeakAlertResponse> detected = leakService.evaluateMeter(meterId);
        return ResponseEntity.ok(ApiResponse.success("Meter evaluation complete", detected));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Investigate, resolve, or flag alert as false positive")
    public ResponseEntity<ApiResponse<LeakAlertResponse>> updateAlertStatus(
            @PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "INVESTIGATING");
        String notes = payload.get("resolutionNotes");
        LeakAlertResponse updated = leakService.updateAlertStatus(id, status, notes);
        return ResponseEntity.ok(ApiResponse.success("Alert status transitioned to " + status, updated));
    }
}
