package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.alert.LeakAlertResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.entity.LeakAlert;
import com.veloia.smartwater.entity.MeterReading;
import com.veloia.smartwater.entity.WaterMeter;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.LeakAlertRepository;
import com.veloia.smartwater.repository.MeterReadingRepository;
import com.veloia.smartwater.repository.WaterMeterRepository;
import com.veloia.smartwater.service.LeakDetectionService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LeakDetectionServiceImpl implements LeakDetectionService {

    private static final Logger log = LoggerFactory.getLogger(LeakDetectionServiceImpl.class);

    private static final Set<String> CLOSED_STATUSES = Set.of("RESOLVED", "FALSE_POSITIVE");
    private static final Set<String> VALID_STATUSES = Set.of("NEW", "ACKNOWLEDGED", "INVESTIGATING", "WORK_ORDER_DISPATCHED", "RESOLVED", "FALSE_POSITIVE");

    private final LeakAlertRepository alertRepository;
    private final WaterMeterRepository meterRepository;
    private final MeterReadingRepository readingRepository;
    private final EntityDtoMapper mapper;

    public LeakDetectionServiceImpl(LeakAlertRepository alertRepository,
                                    WaterMeterRepository meterRepository,
                                    MeterReadingRepository readingRepository,
                                    EntityDtoMapper mapper) {
        this.alertRepository = alertRepository;
        this.meterRepository = meterRepository;
        this.readingRepository = readingRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public List<LeakAlertResponse> runDetectionScan() {
        log.info("Executing rule-based leak detection scan across all active meters");
        List<WaterMeter> activeMeters = meterRepository.findAll().stream()
                .filter(m -> "ACTIVE".equalsIgnoreCase(m.getStatus()))
                .toList();

        List<LeakAlertResponse> generatedAlerts = new ArrayList<>();
        for (WaterMeter meter : activeMeters) {
            generatedAlerts.addAll(evaluateMeter(meter.getId()));
        }
        log.info("Leak detection scan completed. Generated {} new anomaly alerts", generatedAlerts.size());
        return generatedAlerts;
    }

    @Override
    @Transactional
    public List<LeakAlertResponse> evaluateMeter(Long meterId) {
        WaterMeter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", meterId));

        if (alertRepository.existsByMeterIdAndStatusNotIn(meter.getId(), CLOSED_STATUSES)) {
            log.debug("Active/unresolved alert already exists for meter serial {}. Skipping duplicate generation.", meter.getSerialNumber());
            return List.of();
        }

        Page<MeterReading> recentReadings = readingRepository.findByMeterIdOrderByReadingTimestampDesc(
                meter.getId(), PageRequest.of(0, 10));

        if (recentReadings.isEmpty()) {
            return List.of();
        }

        MeterReading latest = recentReadings.getContent().getFirst();
        List<LeakAlert> newAlerts = new ArrayList<>();

        // RULE 1: Burst Pipe Suspicion (High flow rate > 60 LPM or loss > 3.5 m3/h)
        if (latest.getWaterFlowRateLpm() != null && latest.getWaterFlowRateLpm().compareTo(new BigDecimal("60.00")) > 0) {
            newAlerts.add(createAlertEntity(meter, "CRITICAL", "BURST_PIPE_SUSPECTED",
                    new BigDecimal("3.600"),
                    "Sudden acute flow surge of " + latest.getWaterFlowRateLpm() + " LPM detected. Immediate inspection required. [SIMULATED ANOMALY]"));
        }
        // RULE 2: Pressure drop with continuous flow (Pressure < 1.5 bar and flow > 10 LPM)
        else if (latest.getWaterPressureBar() != null && latest.getWaterPressureBar().compareTo(new BigDecimal("1.50")) < 0
                && latest.getWaterFlowRateLpm() != null && latest.getWaterFlowRateLpm().compareTo(new BigDecimal("10.00")) > 0) {
            newAlerts.add(createAlertEntity(meter, "HIGH", "PRESSURE_DROP",
                    new BigDecimal("1.250"),
                    "Secondary pressure drop to " + latest.getWaterPressureBar() + " bar observed during active flow. [SIMULATED ANOMALY]"));
        }
        // RULE 3: Nighttime continuous flow (between 01:00 and 05:00 UTC/local)
        else {
            ZonedDateTime zdt = latest.getReadingTimestamp().atZone(ZoneOffset.UTC);
            int hour = zdt.getHour();
            if ((hour >= 1 && hour <= 5) && latest.getWaterFlowRateLpm() != null && latest.getWaterFlowRateLpm().compareTo(new BigDecimal("12.00")) > 0) {
                newAlerts.add(createAlertEntity(meter, "MEDIUM", "CONTINUOUS_FLOW",
                        new BigDecimal("0.720"),
                        "Persistent nocturnal baseline flow of " + latest.getWaterFlowRateLpm() + " LPM during low-demand hours (" + hour + ":00 UTC). [SIMULATED ANOMALY]"));
            }
        }

        // RULE 4: Sudden increase vs baseline average (if >= 4 readings available)
        if (newAlerts.isEmpty() && recentReadings.getNumberOfElements() >= 4) {
            List<MeterReading> list = recentReadings.getContent();
            BigDecimal sum = BigDecimal.ZERO;
            for (int i = 1; i < list.size(); i++) {
                sum = sum.add(list.get(i).getIncrementalConsumptionM3());
            }
            BigDecimal avgBaseline = sum.divide(BigDecimal.valueOf(list.size() - 1), 3, RoundingMode.HALF_UP);
            if (avgBaseline.compareTo(new BigDecimal("0.100")) > 0
                    && latest.getIncrementalConsumptionM3().compareTo(avgBaseline.multiply(new BigDecimal("3.0"))) > 0) {
                newAlerts.add(createAlertEntity(meter, "HIGH", "CONTINUOUS_FLOW",
                        latest.getIncrementalConsumptionM3().multiply(new BigDecimal("0.50")).setScale(3, RoundingMode.HALF_UP),
                        "Consumption spike: current reading (" + latest.getIncrementalConsumptionM3()
                                + " m3) is > 300% of historical baseline average (" + avgBaseline + " m3). [SIMULATED ANOMALY]"));
            }
        }

        List<LeakAlertResponse> responses = new ArrayList<>();
        for (LeakAlert alert : newAlerts) {
            LeakAlert saved = alertRepository.save(alert);
            responses.add(mapper.toAlertResponse(saved));
        }
        return responses;
    }

    @Override
    @Transactional
    public LeakAlertResponse updateAlertStatus(Long alertId, String status, String resolutionNotes) {
        log.info("Updating leak alert ID {} to status: {}", alertId, status);
        LeakAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("LeakAlert", "id", alertId));

        String targetStatus = status.trim().toUpperCase();
        if (!VALID_STATUSES.contains(targetStatus)) {
            throw new BusinessValidationException("Invalid alert status: " + targetStatus + ". Allowed: " + VALID_STATUSES);
        }

        alert.setStatus(targetStatus);
        if (resolutionNotes != null && !resolutionNotes.isBlank()) {
            alert.setResolutionNotes(resolutionNotes);
        }
        if ("RESOLVED".equalsIgnoreCase(targetStatus) || "FALSE_POSITIVE".equalsIgnoreCase(targetStatus)) {
            alert.setResolvedAt(Instant.now());
        }

        LeakAlert updated = alertRepository.save(alert);
        return mapper.toAlertResponse(updated);
    }

    @Override
    public PageResponse<LeakAlertResponse> getAllAlerts(String status, String severity, Long locationId, Pageable pageable) {
        Specification<LeakAlert> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            if (severity != null && !severity.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("severity")), severity.trim().toUpperCase()));
            }
            if (locationId != null) {
                predicates.add(cb.equal(root.get("location").get("id"), locationId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<LeakAlert> page = alertRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toAlertResponse));
    }

    @Override
    public LeakAlertResponse getAlertById(Long alertId) {
        LeakAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("LeakAlert", "id", alertId));
        return mapper.toAlertResponse(alert);
    }

    @Override
    public LeakAlertResponse getAlertByCode(String alertCode) {
        LeakAlert alert = alertRepository.findByAlertCode(alertCode)
                .orElseThrow(() -> new ResourceNotFoundException("LeakAlert", "alertCode", alertCode));
        return mapper.toAlertResponse(alert);
    }

    private LeakAlert createAlertEntity(WaterMeter meter, String severity, String alertType, BigDecimal lossRate, String evidence) {
        LeakAlert alert = new LeakAlert();
        alert.setAlertCode("ALK-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        alert.setMeter(meter);
        alert.setLocation(meter.getLocation());
        alert.setSeverity(severity);
        alert.setAlertType(alertType);
        alert.setDetectedAt(Instant.now());
        alert.setEstimatedLossRateM3H(lossRate);
        alert.setStatus("NEW");
        alert.setResolutionNotes(evidence);
        return alert;
    }
}
