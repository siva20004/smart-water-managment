package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.region.RegionRequest;
import com.veloia.smartwater.dto.region.RegionResponse;
import com.veloia.smartwater.entity.Region;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.RegionMapper;
import com.veloia.smartwater.repository.RegionRepository;
import com.veloia.smartwater.service.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService {

    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);

    private final RegionRepository regionRepository;
    private final RegionMapper regionMapper;

    public RegionServiceImpl(RegionRepository regionRepository, RegionMapper regionMapper) {
        this.regionRepository = regionRepository;
        this.regionMapper = regionMapper;
    }

    @Override
    @Transactional
    public RegionResponse createRegion(RegionRequest request) {
        log.info("Creating new operational region with code: {}", request.getCode());
        if (regionRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Region", "code", request.getCode());
        }

        Region region = regionMapper.toEntity(request);
        Region saved = regionRepository.save(region);
        log.info("Region created successfully with ID: {}", saved.getId());
        return regionMapper.toResponse(saved);
    }

    @Override
    public RegionResponse getRegionById(Long id) {
        log.debug("Fetching region by ID: {}", id);
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
        return regionMapper.toResponse(region);
    }

    @Override
    public RegionResponse getRegionByCode(String code) {
        log.debug("Fetching region by code: {}", code);
        Region region = regionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "code", code));
        return regionMapper.toResponse(region);
    }

    @Override
    public PageResponse<RegionResponse> getAllRegions(String prefecture, String status, Pageable pageable) {
        log.debug("Querying regions page with filters [prefecture: {}, status: {}]", prefecture, status);
        Page<Region> page;
        if (prefecture != null && !prefecture.isBlank()) {
            page = regionRepository.findByPrefectureIgnoreCase(prefecture.trim(), pageable);
        } else if (status != null && !status.isBlank()) {
            page = regionRepository.findByStatus(status.trim().toUpperCase(), pageable);
        } else {
            page = regionRepository.findAll(pageable);
        }
        return PageResponse.of(page.map(regionMapper::toResponse));
    }

    @Override
    public List<RegionResponse> getActiveRegionsList() {
        return regionRepository.findAll().stream()
                .filter(r -> "ACTIVE".equalsIgnoreCase(r.getStatus()))
                .map(regionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RegionResponse updateRegion(Long id, RegionRequest request) {
        log.info("Updating region ID: {}", id);
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));

        // If code is changing, ensure uniqueness
        if (!region.getCode().equalsIgnoreCase(request.getCode()) && regionRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Region", "code", request.getCode());
        }

        regionMapper.updateEntity(region, request);
        Region updated = regionRepository.save(region);
        log.info("Region ID: {} updated successfully", id);
        return regionMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteRegion(Long id) {
        log.info("Deleting region ID: {}", id);
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
        regionRepository.delete(region);
        log.info("Region ID: {} deleted", id);
    }
}
