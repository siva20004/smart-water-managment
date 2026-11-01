package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.meter.WaterMeterRequest;
import com.veloia.smartwater.dto.meter.WaterMeterResponse;
import com.veloia.smartwater.entity.Customer;
import com.veloia.smartwater.entity.Location;
import com.veloia.smartwater.entity.WaterMeter;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.CustomerRepository;
import com.veloia.smartwater.repository.LocationRepository;
import com.veloia.smartwater.repository.WaterMeterRepository;
import com.veloia.smartwater.service.WaterMeterService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class WaterMeterServiceImpl implements WaterMeterService {

    private static final Logger log = LoggerFactory.getLogger(WaterMeterServiceImpl.class);

    private static final Set<String> VALID_STATUSES = Set.of(
            "ACTIVE", "INACTIVE", "FAULTY", "MAINTENANCE", "RETIRED", "REMOVED"
    );

    private final WaterMeterRepository meterRepository;
    private final CustomerRepository customerRepository;
    private final LocationRepository locationRepository;
    private final EntityDtoMapper mapper;

    public WaterMeterServiceImpl(WaterMeterRepository meterRepository,
                                 CustomerRepository customerRepository,
                                 LocationRepository locationRepository,
                                 EntityDtoMapper mapper) {
        this.meterRepository = meterRepository;
        this.customerRepository = customerRepository;
        this.locationRepository = locationRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public WaterMeterResponse createMeter(WaterMeterRequest request) {
        log.info("Registering water meter with serial: {}", request.getSerialNumber());

        if (meterRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException("WaterMeter", "serialNumber", request.getSerialNumber());
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", request.getLocationId()));

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        }

        WaterMeter meter = new WaterMeter();
        meter.setSerialNumber(request.getSerialNumber().trim());
        meter.setLocation(location);
        meter.setCustomer(customer);
        meter.setMeterModel(request.getMeterModel().trim());
        meter.setMeterType(request.getMeterType().trim().toUpperCase());
        meter.setDiameterMm(request.getDiameterMm());
        meter.setInstallationDate(request.getInstallationDate());
        meter.setStatus(request.getStatus() != null ? request.getStatus().trim().toUpperCase() : "ACTIVE");
        meter.setLastInspectionDate(request.getLastInspectionDate());
        meter.setBatteryLevelPercent(request.getBatteryLevelPercent());
        meter.setLastCommunicationAt(Instant.now());

        WaterMeter saved = meterRepository.save(meter);
        log.info("Meter registered successfully with ID: {}", saved.getId());
        return mapper.toMeterResponse(saved);
    }

    @Override
    public WaterMeterResponse getMeterById(Long id) {
        WaterMeter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", id));
        return mapper.toMeterResponse(meter);
    }

    @Override
    public WaterMeterResponse getMeterBySerial(String serialNumber) {
        WaterMeter meter = meterRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "serialNumber", serialNumber));
        return mapper.toMeterResponse(meter);
    }

    @Override
    public PageResponse<WaterMeterResponse> getAllMeters(String search, String status, Long customerId, Long locationId, Pageable pageable) {
        Specification<WaterMeter> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                Predicate serialPredicate = cb.like(cb.lower(root.get("serialNumber")), pattern);
                Predicate modelPredicate = cb.like(cb.lower(root.get("meterModel")), pattern);

                Join<WaterMeter, Customer> customerJoin = root.join("customer", JoinType.LEFT);
                Predicate customerNamePredicate = cb.like(cb.lower(customerJoin.get("fullName")), pattern);

                predicates.add(cb.or(serialPredicate, modelPredicate, customerNamePredicate));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }

            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }

            if (locationId != null) {
                predicates.add(cb.equal(root.get("location").get("id"), locationId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<WaterMeter> page = meterRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toMeterResponse));
    }

    @Override
    @Transactional
    public WaterMeterResponse updateMeter(Long id, WaterMeterRequest request) {
        log.info("Updating meter ID: {}", id);
        WaterMeter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", id));

        if (!meter.getSerialNumber().equalsIgnoreCase(request.getSerialNumber())
                && meterRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateResourceException("WaterMeter", "serialNumber", request.getSerialNumber());
        }

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", request.getLocationId()));

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));
        }

        meter.setSerialNumber(request.getSerialNumber().trim());
        meter.setLocation(location);
        meter.setCustomer(customer);
        meter.setMeterModel(request.getMeterModel().trim());
        meter.setMeterType(request.getMeterType().trim().toUpperCase());
        meter.setDiameterMm(request.getDiameterMm());
        meter.setInstallationDate(request.getInstallationDate());
        if (request.getStatus() != null) {
            validateStatusTransition(meter.getStatus(), request.getStatus().trim().toUpperCase());
            meter.setStatus(request.getStatus().trim().toUpperCase());
        }
        meter.setLastInspectionDate(request.getLastInspectionDate());
        meter.setBatteryLevelPercent(request.getBatteryLevelPercent());

        WaterMeter updated = meterRepository.save(meter);
        return mapper.toMeterResponse(updated);
    }

    @Override
    @Transactional
    public WaterMeterResponse assignMeterToCustomer(Long meterId, Long customerId) {
        log.info("Assigning meter ID {} to customer ID {}", meterId, customerId);
        WaterMeter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", meterId));

        if (customerId == null) {
            meter.setCustomer(null);
        } else {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));
            if (!"ACTIVE".equalsIgnoreCase(customer.getStatus())) {
                throw new BusinessValidationException("Cannot assign water meter to an inactive/suspended customer account");
            }
            meter.setCustomer(customer);
        }

        WaterMeter updated = meterRepository.save(meter);
        return mapper.toMeterResponse(updated);
    }

    @Override
    @Transactional
    public WaterMeterResponse setMeterStatus(Long meterId, String status) {
        log.info("Changing meter status [ID: {}, Status: {}]", meterId, status);
        WaterMeter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", meterId));

        String targetStatus = status.trim().toUpperCase();
        validateStatusTransition(meter.getStatus(), targetStatus);

        meter.setStatus(targetStatus);
        WaterMeter updated = meterRepository.save(meter);
        return mapper.toMeterResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMeter(Long meterId) {
        log.info("Deleting meter ID: {}", meterId);
        WaterMeter meter = meterRepository.findById(meterId)
                .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", meterId));

        if (meter.getReadings() != null && !meter.getReadings().isEmpty()) {
            meter.setStatus("REMOVED");
            meterRepository.save(meter);
            log.info("Meter ID {} has historical telemetry readings; status transitioned to REMOVED instead of physical deletion.", meterId);
        } else {
            meterRepository.delete(meter);
        }
    }

    private void validateStatusTransition(String currentStatus, String targetStatus) {
        if (!VALID_STATUSES.contains(targetStatus)) {
            throw new BusinessValidationException("Invalid meter status: '" + targetStatus + "'. Allowed: " + VALID_STATUSES);
        }
        if ("RETIRED".equalsIgnoreCase(currentStatus) && "ACTIVE".equalsIgnoreCase(targetStatus)) {
            throw new BusinessValidationException("Retired meters cannot be directly reactivated; commission a new instrument.");
        }
    }
}
