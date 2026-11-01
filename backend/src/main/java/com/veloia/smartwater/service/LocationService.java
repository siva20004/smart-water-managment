package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.location.LocationRequest;
import com.veloia.smartwater.dto.location.LocationResponse;
import org.springframework.data.domain.Pageable;

public interface LocationService {

    LocationResponse createLocation(LocationRequest request);

    LocationResponse getLocationById(Long id);

    LocationResponse getLocationByCode(String code);

    PageResponse<LocationResponse> getAllLocations(Long regionId, String city, String installationType, Pageable pageable);

    LocationResponse updateLocation(Long id, LocationRequest request);

    void deleteLocation(Long id);
}
