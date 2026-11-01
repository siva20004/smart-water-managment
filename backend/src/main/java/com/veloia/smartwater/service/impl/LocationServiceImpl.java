package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.location.LocationRequest;
import com.veloia.smartwater.dto.location.LocationResponse;
import com.veloia.smartwater.entity.Location;
import com.veloia.smartwater.entity.Region;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.LocationMapper;
import com.veloia.smartwater.repository.LocationRepository;
import com.veloia.smartwater.repository.RegionRepository;
import com.veloia.smartwater.service.LocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

    private final LocationRepository locationRepository;
    private final RegionRepository regionRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository,
                               RegionRepository regionRepository,
                               LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.regionRepository = regionRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        log.info("Creating location with code: {}", request.getLocationCode());
        if (locationRepository.existsByLocationCode(request.getLocationCode())) {
            throw new DuplicateResourceException("Location", "locationCode", request.getLocationCode());
        }

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getRegionId()));

        Location location = locationMapper.toEntity(request, region);
        Location saved = locationRepository.save(location);
        log.info("Location created successfully with ID: {}", saved.getId());
        return locationMapper.toResponse(saved);
    }

    @Override
    public LocationResponse getLocationById(Long id) {
        log.debug("Fetching location by ID: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));
        return locationMapper.toResponse(location);
    }

    @Override
    public LocationResponse getLocationByCode(String code) {
        log.debug("Fetching location by code: {}", code);
        Location location = locationRepository.findByLocationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "locationCode", code));
        return locationMapper.toResponse(location);
    }

    @Override
    public PageResponse<LocationResponse> getAllLocations(Long regionId, String city, String installationType, Pageable pageable) {
        log.debug("Querying locations page [regionId: {}, city: {}, installationType: {}]", regionId, city, installationType);
        Page<Location> page;
        if (regionId != null) {
            page = locationRepository.findByRegionId(regionId, pageable);
        } else if (city != null && !city.isBlank()) {
            page = locationRepository.findByCityIgnoreCase(city.trim(), pageable);
        } else if (installationType != null && !installationType.isBlank()) {
            page = locationRepository.findByInstallationType(installationType.trim().toUpperCase(), pageable);
        } else {
            page = locationRepository.findAll(pageable);
        }
        return PageResponse.of(page.map(locationMapper::toResponse));
    }

    @Override
    @Transactional
    public LocationResponse updateLocation(Long id, LocationRequest request) {
        log.info("Updating location ID: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));

        if (!location.getLocationCode().equalsIgnoreCase(request.getLocationCode()) &&
                locationRepository.existsByLocationCode(request.getLocationCode())) {
            throw new DuplicateResourceException("Location", "locationCode", request.getLocationCode());
        }

        Region region = null;
        if (!location.getRegion().getId().equals(request.getRegionId())) {
            region = regionRepository.findById(request.getRegionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Region", "id", request.getRegionId()));
        }

        locationMapper.updateEntity(location, request, region);
        Location updated = locationRepository.save(location);
        log.info("Location ID: {} updated successfully", id);
        return locationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(Long id) {
        log.info("Deleting location ID: {}", id);
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", id));
        locationRepository.delete(location);
        log.info("Location ID: {} deleted", id);
    }
}
