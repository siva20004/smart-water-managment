package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.meter.WaterMeterRequest;
import com.veloia.smartwater.dto.meter.WaterMeterResponse;
import com.veloia.smartwater.service.WaterMeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meters")
@Tag(name = "Water Meters", description = "Physical & AMI measurement instruments, inventory, customer assignments, and telemetry health")
public class WaterMeterController {

    private final WaterMeterService meterService;

    public WaterMeterController(WaterMeterService meterService) {
        this.meterService = meterService;
    }

    @PostMapping
    @Operation(summary = "Register a new water meter in the utility asset registry")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> createMeter(@Valid @RequestBody WaterMeterRequest request) {
        WaterMeterResponse created = meterService.createMeter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Meter registered successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated water meters with search and filters (ACTIVE, INACTIVE, FAULTY, REMOVED)")
    public ResponseEntity<ApiResponse<PageResponse<WaterMeterResponse>>> getAllMeters(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long locationId,
            @PageableDefault(size = 10, sort = "serialNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<WaterMeterResponse> page = meterService.getAllMeters(search, status, customerId, locationId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get water meter by ID")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> getMeterById(@PathVariable Long id) {
        WaterMeterResponse meter = meterService.getMeterById(id);
        return ResponseEntity.ok(ApiResponse.success(meter));
    }

    @GetMapping("/serial/{serialNumber}")
    @Operation(summary = "Get water meter by serial number")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> getMeterBySerial(@PathVariable String serialNumber) {
        WaterMeterResponse meter = meterService.getMeterBySerial(serialNumber);
        return ResponseEntity.ok(ApiResponse.success(meter));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update meter technical and asset attributes")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> updateMeter(
            @PathVariable Long id, @Valid @RequestBody WaterMeterRequest request) {
        WaterMeterResponse updated = meterService.updateMeter(id, request);
        return ResponseEntity.ok(ApiResponse.success("Meter updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change meter operational status (ACTIVE, INACTIVE, FAULTY, REMOVED)")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> setMeterStatus(
            @PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.getOrDefault("status", "ACTIVE");
        WaterMeterResponse updated = meterService.setMeterStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Meter status updated to " + status, updated));
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign or reassign meter to a customer")
    public ResponseEntity<ApiResponse<WaterMeterResponse>> assignMeterToCustomer(
            @PathVariable Long id, @RequestBody Map<String, Long> payload) {
        Long customerId = payload.get("customerId");
        WaterMeterResponse updated = meterService.assignMeterToCustomer(id, customerId);
        return ResponseEntity.ok(ApiResponse.success("Meter assignment updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete or retire a water meter from active service")
    public ResponseEntity<ApiResponse<Void>> deleteMeter(@PathVariable Long id) {
        meterService.deleteMeter(id);
        return ResponseEntity.ok(ApiResponse.success("Meter retired/deleted successfully", null));
    }
}
