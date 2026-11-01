package com.veloia.smartwater.mapper;

import com.veloia.smartwater.dto.location.LocationRequest;
import com.veloia.smartwater.dto.location.LocationResponse;
import com.veloia.smartwater.entity.Location;
import com.veloia.smartwater.entity.Region;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationRequest request, Region region) {
        if (request == null) return null;
        Location location = new Location();
        location.setRegion(region);
        location.setLocationCode(request.getLocationCode());
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setPostalCode(request.getPostalCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setElevationMeters(request.getElevationMeters());
        location.setInstallationType(request.getInstallationType());
        return location;
    }

    public void updateEntity(Location location, LocationRequest request, Region region) {
        if (location == null || request == null) return;
        if (region != null) {
            location.setRegion(region);
        }
        location.setAddressLine1(request.getAddressLine1());
        location.setAddressLine2(request.getAddressLine2());
        location.setCity(request.getCity());
        location.setPostalCode(request.getPostalCode());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setElevationMeters(request.getElevationMeters());
        location.setInstallationType(request.getInstallationType());
    }

    public LocationResponse toResponse(Location location) {
        if (location == null) return null;
        LocationResponse response = new LocationResponse();
        response.setId(location.getId());
        if (location.getRegion() != null) {
            response.setRegionId(location.getRegion().getId());
            response.setRegionCode(location.getRegion().getCode());
            response.setRegionName(location.getRegion().getName());
        }
        response.setLocationCode(location.getLocationCode());
        response.setAddressLine1(location.getAddressLine1());
        response.setAddressLine2(location.getAddressLine2());
        response.setCity(location.getCity());
        response.setPostalCode(location.getPostalCode());
        response.setLatitude(location.getLatitude());
        response.setLongitude(location.getLongitude());
        response.setElevationMeters(location.getElevationMeters());
        response.setInstallationType(location.getInstallationType());
        response.setMeterCount(location.getMeters() != null ? location.getMeters().size() : 0);
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
}
