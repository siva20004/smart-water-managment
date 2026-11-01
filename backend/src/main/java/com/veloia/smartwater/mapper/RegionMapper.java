package com.veloia.smartwater.mapper;

import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import com.veloia.smartwater.entity.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionMapper {

    public Region toEntity(RegionRequest request) {
        if (request == null) return null;
        Region region = new Region();
        region.setCode(request.getCode());
        region.setName(request.getName());
        region.setPrefecture(request.getPrefecture());
        region.setDescription(request.getDescription());
        region.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        return region;
    }

    public void updateEntity(Region region, RegionRequest request) {
        if (region == null || request == null) return;
        region.setName(request.getName());
        region.setPrefecture(request.getPrefecture());
        region.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            region.setStatus(request.getStatus());
        }
    }

    public RegionResponse toResponse(Region region) {
        if (region == null) return null;
        RegionResponse response = new RegionResponse();
        response.setId(region.getId());
        response.setCode(region.getCode());
        response.setName(region.getName());
        response.setPrefecture(region.getPrefecture());
        response.setDescription(region.getDescription());
        response.setStatus(region.getStatus());
        response.setLocationCount(region.getLocations() != null ? region.getLocations().size() : 0);
        response.setCreatedAt(region.getCreatedAt());
        response.setUpdatedAt(region.getUpdatedAt());
        return response;
    }
}
