package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.quality.WaterQualityRequest;
import com.veloia.smartwater.dto.quality.WaterQualityResponse;
import com.veloia.smartwater.dto.quality.WaterQualityStatsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WaterQualityService {

    WaterQualityResponse recordSample(WaterQualityRequest request);

    PageResponse<WaterQualityResponse> getAllSamples(Long locationId, String potabilityStatus, Pageable pageable);

    WaterQualityResponse getSampleById(Long id);

    WaterQualityStatsDto getQualityStatistics();

    List<WaterQualityResponse> getRecentTrends(int limit);
}
