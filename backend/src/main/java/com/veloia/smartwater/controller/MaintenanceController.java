package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.maintenance.CreateMaintenanceRequest;
import com.veloia.smartwater.dto.maintenance.MaintenanceRequestResponse;
import com.veloia.smartwater.dto.maintenance.UpdateWorkOrderStatusRequest;
import com.veloia.smartwater.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance")
@Tag(name = "Maintenance Requests", description = "Field dispatch, pipe repair, acoustic investigation, and technician work orders")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping
    @Operation(summary = "Create a new maintenance work order")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> createRequest(
            @Valid @RequestBody CreateMaintenanceRequest request) {
        MaintenanceRequestResponse created = maintenanceService.createRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Work order created successfully", created));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update maintenance status (PENDING -> ASSIGNED -> IN_PROGRESS -> COMPLETED) and technician")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> updateStatus(
            @PathVariable Long id, @Valid @RequestBody UpdateWorkOrderStatusRequest request) {
        MaintenanceRequestResponse updated = maintenanceService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Work order status updated", updated));
    }

    @GetMapping
    @Operation(summary = "Get paginated maintenance work orders with status, priority, and technician filters")
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceRequestResponse>>> getAllMaintenanceRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long technicianId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<MaintenanceRequestResponse> page = maintenanceService.getAllRequests(status, priority, technicianId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get maintenance request by ID")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> getMaintenanceById(@PathVariable Long id) {
        MaintenanceRequestResponse request = maintenanceService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request));
    }

    @GetMapping("/number/{requestNumber}")
    @Operation(summary = "Get maintenance request by unique request number")
    public ResponseEntity<ApiResponse<MaintenanceRequestResponse>> getMaintenanceByRequestNumber(
            @PathVariable String requestNumber) {
        MaintenanceRequestResponse request = maintenanceService.getRequestByNumber(requestNumber);
        return ResponseEntity.ok(ApiResponse.success(request));
    }
}
