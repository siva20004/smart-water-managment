package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import com.veloia.smartwater.service.RegionService;
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
@RequestMapping("/api/regions")
@Tag(name = "Regions", description = "Operational water utility sector and prefecture management")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @PostMapping
    @Operation(summary = "Create a new utility operational region")
    public ResponseEntity<ApiResponse<RegionResponse>> createRegion(@Valid @RequestBody RegionRequest request) {
        RegionResponse created = regionService.createRegion(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Region created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated operational regions with optional prefecture or status filters")
    public ResponseEntity<ApiResponse<PageResponse<RegionResponse>>> getAllRegions(
            @RequestParam(required = false) String prefecture,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "code", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<RegionResponse> page = regionService.getAllRegions(prefecture, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/active")
    @Operation(summary = "Get list of all active operational regions")
    public ResponseEntity<ApiResponse<List<RegionResponse>>> getActiveRegions() {
        List<RegionResponse> list = regionService.getActiveRegionsList();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get region details by ID")
    public ResponseEntity<ApiResponse<RegionResponse>> getRegionById(@PathVariable Long id) {
        RegionResponse region = regionService.getRegionById(id);
        return ResponseEntity.ok(ApiResponse.success(region));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get region details by unique code")
    public ResponseEntity<ApiResponse<RegionResponse>> getRegionByCode(@PathVariable String code) {
        RegionResponse region = regionService.getRegionByCode(code);
        return ResponseEntity.ok(ApiResponse.success(region));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing operational region")
    public ResponseEntity<ApiResponse<RegionResponse>> updateRegion(
            @PathVariable Long id, @Valid @RequestBody RegionRequest request) {
        RegionResponse updated = regionService.updateRegion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Region updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an operational region")
    public ResponseEntity<ApiResponse<Void>> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.ok(ApiResponse.success("Region deleted successfully", null));
    }
}
