package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.quality.WaterQualityRequest;
import com.veloia.smartwater.dto.quality.WaterQualityResponse;
import com.veloia.smartwater.dto.quality.WaterQualityStatsDto;
import com.veloia.smartwater.entity.Location;
import com.veloia.smartwater.entity.WaterQualityRecord;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.LocationRepository;
import com.veloia.smartwater.repository.WaterQualityRecordRepository;
import com.veloia.smartwater.service.WaterQualityService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WaterQualityServiceImpl implements WaterQualityService {

    private static final Logger log = LoggerFactory.getLogger(WaterQualityServiceImpl.class);

    private final WaterQualityRecordRepository qualityRepository;
    private final LocationRepository locationRepository;
    private final EntityDtoMapper mapper;

    public WaterQualityServiceImpl(WaterQualityRecordRepository qualityRepository,
                                   LocationRepository locationRepository,
                                   EntityDtoMapper mapper) {
        this.qualityRepository = qualityRepository;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public WaterQualityResponse recordSample(WaterQualityRequest request) {
        log.info("Recording water quality lab sample for location ID: {}", request.getLocationId());

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", request.getLocationId()));

        String potability = evaluatePotabilityStatus(
                request.getPhLevel(), request.getTurbidityNtu(), request.getResidualChlorineMgL());

        String sampleCode = "WQS-" + DateTimeFormatter.ofPattern("yyyyMM").format(java.time.LocalDate.now())
                + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        WaterQualityRecord record = new WaterQualityRecord();
        record.setSampleCode(sampleCode);
        record.setLocation(location);
        record.setSampleTimestamp(request.getSampleTimestamp());
        record.setPhLevel(request.getPhLevel());
        record.setTurbidityNtu(request.getTurbidityNtu());
        record.setResidualChlorineMgL(request.getResidualChlorineMgL());
        record.setDissolvedOxygenMgL(request.getDissolvedOxygenMgL());
        record.setConductivityUsCm(request.getConductivityUsCm());
        record.setWaterTemperatureCelsius(request.getWaterTemperatureCelsius());
        record.setPotabilityStatus(potability);
        record.setSampledBy(request.getSampledBy().trim());

        WaterQualityRecord saved = qualityRepository.save(record);
        log.info("Sample record {} saved with status: {}", saved.getSampleCode(), potability);
        return mapper.toQualityResponse(saved);
    }

    @Override
    public PageResponse<WaterQualityResponse> getAllSamples(Long locationId, String potabilityStatus, Pageable pageable) {
        Specification<WaterQualityRecord> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (locationId != null) {
                predicates.add(cb.equal(root.get("location").get("id"), locationId));
            }
            if (potabilityStatus != null && !potabilityStatus.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("potabilityStatus")), potabilityStatus.trim().toUpperCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<WaterQualityRecord> page = qualityRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toQualityResponse));
    }

    @Override
    public WaterQualityResponse getSampleById(Long id) {
        WaterQualityRecord record = qualityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterQualityRecord", "id", id));
        return mapper.toQualityResponse(record);
    }

    @Override
    public WaterQualityStatsDto getQualityStatistics() {
        List<WaterQualityRecord> records = qualityRepository.findAll();
        WaterQualityStatsDto stats = new WaterQualityStatsDto();
        stats.setTotalSamples(records.size());

        if (records.isEmpty()) {
            stats.setComplianceRatePercent(BigDecimal.valueOf(100.00));
            stats.setAvgPh(BigDecimal.valueOf(7.40));
            stats.setAvgTurbidityNtu(BigDecimal.valueOf(0.150));
            stats.setAvgChlorineMgL(BigDecimal.valueOf(0.450));
            return stats;
        }

        long potable = 0;
        long monitoring = 0;
        long nonPotable = 0;
        BigDecimal sumPh = BigDecimal.ZERO;
        BigDecimal sumTurb = BigDecimal.ZERO;
        BigDecimal sumChlorine = BigDecimal.ZERO;

        for (WaterQualityRecord r : records) {
            sumPh = sumPh.add(r.getPhLevel());
            sumTurb = sumTurb.add(r.getTurbidityNtu());
            sumChlorine = sumChlorine.add(r.getResidualChlorineMgL());

            if ("POTABLE".equalsIgnoreCase(r.getPotabilityStatus())) {
                potable++;
            } else if ("POTABLE_MONITORING_REQUIRED".equalsIgnoreCase(r.getPotabilityStatus())) {
                monitoring++;
            } else {
                nonPotable++;
            }
        }

        BigDecimal count = BigDecimal.valueOf(records.size());
        stats.setPotableCount(potable);
        stats.setMonitoringRequiredCount(monitoring);
        stats.setNonPotableCount(nonPotable);
        stats.setComplianceRatePercent(BigDecimal.valueOf(potable).multiply(BigDecimal.valueOf(100)).divide(count, 2, RoundingMode.HALF_UP));
        stats.setAvgPh(sumPh.divide(count, 2, RoundingMode.HALF_UP));
        stats.setAvgTurbidityNtu(sumTurb.divide(count, 3, RoundingMode.HALF_UP));
        stats.setAvgChlorineMgL(sumChlorine.divide(count, 3, RoundingMode.HALF_UP));
        return stats;
    }

    @Override
    public List<WaterQualityResponse> getRecentTrends(int limit) {
        Page<WaterQualityRecord> page = qualityRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "sampleTimestamp")));
        return page.map(mapper::toQualityResponse).getContent();
    }

    private String evaluatePotabilityStatus(BigDecimal ph, BigDecimal turb, BigDecimal chlorine) {
        // Severe violation
        if (ph.compareTo(new BigDecimal("6.00")) < 0 || ph.compareTo(new BigDecimal("9.00")) > 0
                || turb.compareTo(new BigDecimal("5.000")) > 0
                || chlorine.compareTo(new BigDecimal("0.050")) < 0) {
            return "NON_POTABLE";
        }
        // Moderate / monitoring required
        if (ph.compareTo(new BigDecimal("6.50")) < 0 || ph.compareTo(new BigDecimal("8.50")) > 0
                || turb.compareTo(new BigDecimal("1.500")) > 0
                || chlorine.compareTo(new BigDecimal("0.100")) < 0 || chlorine.compareTo(new BigDecimal("1.500")) > 0) {
            return "POTABLE_MONITORING_REQUIRED";
        }
        return "POTABLE";
    }
}
