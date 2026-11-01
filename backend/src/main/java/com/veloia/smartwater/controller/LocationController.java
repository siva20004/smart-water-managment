package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.location.LocationRequest;
import com.veloia.smartwater.dto.location.LocationResponse;
import com.veloia.smartwater.service.LocationService;
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
@RequestMapping("/api/locations")
@Tag(name = "Locations", description = "Physical installation points, facility coordinates, and addresses")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    @Operation(summary = "Register a new physical installation location")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@Valid @RequestBody LocationRequest request) {
        LocationResponse created = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Location registered successfully", created));
    }

    @GetMapping
    @Operation(summary = "Get paginated locations with optional filtering by region, city, or installation type")
    public ResponseEntity<ApiResponse<PageResponse<LocationResponse>>> getAllLocations(
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String installationType,
            @PageableDefault(size = 10, sort = "locationCode", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse<LocationResponse> page = locationService.getAllLocations(regionId, city, installationType, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location details by ID")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationById(@PathVariable Long id) {
        LocationResponse location = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get location details by location code")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationByCode(@PathVariable String code) {
        LocationResponse location = locationService.getLocationByCode(code);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @PathVariable Long id, @Valid @RequestBody LocationRequest request) {
        LocationResponse updated = locationService.updateLocation(id, request);
        return ResponseEntity.ok(ApiResponse.success("Location updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a location")
    public ResponseEntity<ApiResponse<Void>> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(ApiResponse.success("Location deleted successfully", null));
    }
}
