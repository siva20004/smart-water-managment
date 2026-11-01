package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RegionService {

    RegionResponse createRegion(RegionRequest request);

    RegionResponse getRegionById(Long id);

    RegionResponse getRegionByCode(String code);

    PageResponse<RegionResponse> getAllRegions(String prefecture, String status, Pageable pageable);

    List<RegionResponse> getActiveRegionsList();

    RegionResponse updateRegion(Long id, RegionRequest request);

    void deleteRegion(Long id);
}
