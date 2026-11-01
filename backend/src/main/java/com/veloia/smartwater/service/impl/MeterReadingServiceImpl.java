package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.analytics.ConsumptionAnalyticsDtos.*;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.reading.MeterReadingRequest;
import com.veloia.smartwater.dto.reading.MeterReadingResponse;
import com.veloia.smartwater.entity.MeterReading;
import com.veloia.smartwater.entity.WaterMeter;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.MeterReadingRepository;
import com.veloia.smartwater.repository.WaterMeterRepository;
import com.veloia.smartwater.service.MeterReadingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MeterReadingServiceImpl implements MeterReadingService {

    private static final Logger log = LoggerFactory.getLogger(MeterReadingServiceImpl.class);

    private final MeterReadingRepository readingRepository;
    private final WaterMeterRepository meterRepository;
    private final EntityDtoMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    public MeterReadingServiceImpl(MeterReadingRepository readingRepository,
                                   WaterMeterRepository meterRepository,
                                   EntityDtoMapper mapper) {
        this.readingRepository = readingRepository;
        this.meterRepository = meterRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public MeterReadingResponse submitReading(MeterReadingRequest request) {
        log.info("Submitting telemetry reading for meter ID: {}", request.getMeterId());

        WaterMeter meter = meterRepository.findById(request.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", request.getMeterId()));

        if ("RETIRED".equalsIgnoreCase(meter.getStatus()) || "REMOVED".equalsIgnoreCase(meter.getStatus())) {
            throw new BusinessValidationException("Cannot submit readings for meter with status: " + meter.getStatus());
        }

        if (request.getCumulativeReadingM3().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Cumulative reading cannot be negative");
        }

        if (readingRepository.existsByMeterIdAndReadingTimestamp(meter.getId(), request.getReadingTimestamp())) {
            throw new DuplicateResourceException("MeterReading", "timestamp",
                    "Meter " + meter.getSerialNumber() + " already has a reading at " + request.getReadingTimestamp());
        }

        Optional<MeterReading> prevReadingOpt = readingRepository.findTopByMeterIdOrderByReadingTimestampDesc(meter.getId());
        BigDecimal incrementalConsumption = BigDecimal.ZERO;
        String notes = request.getValidationNotes();
        boolean isValidated = true;

        if (prevReadingOpt.isPresent()) {
            MeterReading prev = prevReadingOpt.get();
            if (request.getCumulativeReadingM3().compareTo(prev.getCumulativeReadingM3()) < 0) {
                if (request.isMeterReset()) {
                    incrementalConsumption = request.getCumulativeReadingM3();
                    notes = (notes != null && !notes.isBlank()) ? notes + " [METER_RESET_CONFIRMED]" : "Meter reset or replacement confirmed";
                } else {
                    throw new BusinessValidationException("Decreasing cumulative reading detected ("
                            + request.getCumulativeReadingM3() + " m3 < previous " + prev.getCumulativeReadingM3()
                            + " m3). If this was an intentional hardware replacement or rollover, check the 'Meter Reset' flag.");
                }
            } else {
                incrementalConsumption = request.getCumulativeReadingM3().subtract(prev.getCumulativeReadingM3());
            }
        }

        MeterReading reading = new MeterReading();
        reading.setMeter(meter);
        reading.setReadingTimestamp(request.getReadingTimestamp());
        reading.setCumulativeReadingM3(request.getCumulativeReadingM3());
        reading.setIncrementalConsumptionM3(incrementalConsumption);
        reading.setWaterFlowRateLpm(request.getWaterFlowRateLpm());
        reading.setWaterPressureBar(request.getWaterPressureBar());
        reading.setWaterTemperatureCelsius(request.getWaterTemperatureCelsius());
        reading.setReadingSource(request.getReadingSource() != null ? request.getReadingSource().toUpperCase() : "AMI_AUTOMATED");
        reading.setIsValidated(isValidated);
        reading.setValidationNotes(notes);

        MeterReading saved = readingRepository.save(reading);

        // Update meter last communication timestamp
        meter.setLastCommunicationAt(request.getReadingTimestamp());
        meterRepository.save(meter);

        log.info("Reading ID {} recorded. Incremental consumption: {} m3", saved.getId(), incrementalConsumption);
        return mapper.toReadingResponse(saved);
    }

    @Override
    public PageResponse<MeterReadingResponse> getReadingsByMeter(Long meterId, Pageable pageable) {
        Page<MeterReading> page = readingRepository.findByMeterIdOrderByReadingTimestampDesc(meterId, pageable);
        return PageResponse.of(page.map(mapper::toReadingResponse));
    }

    @Override
    public PageResponse<MeterReadingResponse> getAllReadings(Long meterId, Pageable pageable) {
        Page<MeterReading> page = (meterId != null)
                ? readingRepository.findByMeterIdOrderByReadingTimestampDesc(meterId, pageable)
                : readingRepository.findAll(pageable);
        return PageResponse.of(page.map(mapper::toReadingResponse));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DailyConsumption> getDailyConsumption(int days) {
        String sql = """
            SELECT TO_CHAR(reading_timestamp, 'YYYY-MM-DD') AS reading_date,
                   COALESCE(SUM(incremental_consumption_m3), 0.000) AS total_consumption,
                   COUNT(id) AS reading_count
            FROM meter_readings
            GROUP BY TO_CHAR(reading_timestamp, 'YYYY-MM-DD')
            ORDER BY reading_date DESC
            LIMIT :days
            """;
        try {
            List<Object[]> rows = entityManager.createNativeQuery(sql)
                    .setParameter("days", days)
                    .getResultList();

            List<DailyConsumption> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(new DailyConsumption(
                        (String) row[0],
                        new BigDecimal(row[1].toString()),
                        ((Number) row[2]).longValue()
                ));
            }
            return result;
        } catch (Exception e) {
            log.warn("Native query failed, falling back to simulated aggregations: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MonthlyConsumption> getMonthlyConsumption() {
        String sql = """
            SELECT TO_CHAR(reading_timestamp, 'YYYY-MM') AS year_month,
                   COALESCE(SUM(incremental_consumption_m3), 0.000) AS total_consumption,
                   COUNT(DISTINCT meter_id) AS active_meters
            FROM meter_readings
            GROUP BY TO_CHAR(reading_timestamp, 'YYYY-MM')
            ORDER BY year_month ASC
            """;
        try {
            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            List<MonthlyConsumption> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(new MonthlyConsumption(
                        (String) row[0],
                        new BigDecimal(row[1].toString()),
                        ((Number) row[2]).longValue()
                ));
            }
            return result;
        } catch (Exception e) {
            log.warn("Monthly consumption query error: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CustomerConsumption> getCustomerConsumption() {
        String sql = """
            SELECT c.id, c.customer_number, c.full_name,
                   COALESCE(SUM(r.incremental_consumption_m3), 0.000) AS total_consumption
            FROM customers c
            JOIN water_meters m ON m.customer_id = c.id
            JOIN meter_readings r ON r.meter_id = m.id
            GROUP BY c.id, c.customer_number, c.full_name
            ORDER BY total_consumption DESC
            LIMIT 10
            """;
        try {
            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            List<CustomerConsumption> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(new CustomerConsumption(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        new BigDecimal(row[3].toString())
                ));
            }
            return result;
        } catch (Exception e) {
            log.warn("Customer consumption query error: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RegionConsumption> getRegionConsumption() {
        String sql = """
            SELECT reg.id, reg.code, reg.name,
                   COALESCE(SUM(r.incremental_consumption_m3), 0.000) AS total_consumption
            FROM regions reg
            JOIN locations l ON l.region_id = reg.id
            JOIN water_meters m ON m.location_id = l.id
            JOIN meter_readings r ON r.meter_id = m.id
            GROUP BY reg.id, reg.code, reg.name
            ORDER BY total_consumption DESC
            """;
        try {
            List<Object[]> rows = entityManager.createNativeQuery(sql).getResultList();
            List<RegionConsumption> result = new ArrayList<>();
            for (Object[] row : rows) {
                result.add(new RegionConsumption(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        new BigDecimal(row[3].toString())
                ));
            }
            return result;
        } catch (Exception e) {
            log.warn("Region consumption query error: {}", e.getMessage());
            return List.of();
        }
    }
}
