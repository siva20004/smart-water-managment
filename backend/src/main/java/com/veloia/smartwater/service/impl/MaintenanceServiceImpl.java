package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.maintenance.CreateMaintenanceRequest;
import com.veloia.smartwater.dto.maintenance.MaintenanceRequestResponse;
import com.veloia.smartwater.dto.maintenance.UpdateWorkOrderStatusRequest;
import com.veloia.smartwater.entity.*;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.*;
import com.veloia.smartwater.service.MaintenanceService;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MaintenanceServiceImpl implements MaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceServiceImpl.class);

    private final MaintenanceRequestRepository maintenanceRepository;
    private final LocationRepository locationRepository;
    private final WaterMeterRepository meterRepository;
    private final LeakAlertRepository alertRepository;
    private final AppUserRepository userRepository;
    private final EntityDtoMapper mapper;

    public MaintenanceServiceImpl(MaintenanceRequestRepository maintenanceRepository,
                                  LocationRepository locationRepository,
                                  WaterMeterRepository meterRepository,
                                  LeakAlertRepository alertRepository,
                                  AppUserRepository userRepository,
                                  EntityDtoMapper mapper) {
        this.maintenanceRepository = maintenanceRepository;
        this.locationRepository = locationRepository;
        this.meterRepository = meterRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public MaintenanceRequestResponse createRequest(CreateMaintenanceRequest request) {
        log.info("Creating maintenance work order: '{}'", request.getTitle());

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location", "id", request.getLocationId()));

        WaterMeter meter = null;
        if (request.getMeterId() != null) {
            meter = meterRepository.findById(request.getMeterId())
                    .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", request.getMeterId()));
        }

        LeakAlert alert = null;
        if (request.getAlertId() != null) {
            alert = alertRepository.findById(request.getAlertId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeakAlert", "id", request.getAlertId()));
        }

        AppUser technician = null;
        if (request.getAssignedTechnicianId() != null) {
            technician = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("AppUser", "id", request.getAssignedTechnicianId()));
        }

        String requestNumber = "WO-" + DateTimeFormatter.ofPattern("yyyyMM").format(java.time.LocalDate.now())
                + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        MaintenanceRequest mr = new MaintenanceRequest();
        mr.setRequestNumber(requestNumber);
        mr.setLocation(location);
        mr.setMeter(meter);
        mr.setAlert(alert);
        mr.setAssignedTechnician(technician);
        mr.setPriority(request.getPriority().toUpperCase());
        mr.setCategory(request.getCategory().toUpperCase());
        mr.setTitle(request.getTitle().trim());
        mr.setDescription(request.getDescription().trim());
        mr.setStatus(technician != null ? "ASSIGNED" : "PENDING");
        mr.setScheduledStart(request.getScheduledStart() != null ? request.getScheduledStart() : Instant.now());
        mr.setPartsCost(BigDecimal.ZERO);
        mr.setLaborHours(BigDecimal.ZERO);

        MaintenanceRequest saved = maintenanceRepository.save(mr);

        if (alert != null) {
            alert.setStatus("WORK_ORDER_DISPATCHED");
            alertRepository.save(alert);
        }

        log.info("Work order {} created with initial status: {}", saved.getRequestNumber(), saved.getStatus());
        return mapper.toMaintenanceResponse(saved);
    }

    @Override
    @Transactional
    public MaintenanceRequestResponse updateStatus(Long id, UpdateWorkOrderStatusRequest request) {
        log.info("Updating work order ID {} to status: {}", id, request.getStatus());
        MaintenanceRequest mr = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "id", id));

        String targetStatus = request.getStatus().trim().toUpperCase();

        if (request.getAssignedTechnicianId() != null) {
            AppUser tech = userRepository.findById(request.getAssignedTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("AppUser", "id", request.getAssignedTechnicianId()));
            mr.setAssignedTechnician(tech);
        }

        if (request.getResolutionSummary() != null && !request.getResolutionSummary().isBlank()) {
            mr.setResolutionSummary(request.getResolutionSummary().trim());
        }
        if (request.getPartsCost() != null) {
            mr.setPartsCost(request.getPartsCost());
        }
        if (request.getLaborHours() != null) {
            mr.setLaborHours(request.getLaborHours());
        }

        mr.setStatus(targetStatus);

        if ("COMPLETED".equalsIgnoreCase(targetStatus)) {
            mr.setActualCompletion(Instant.now());
            if (mr.getAlert() != null) {
                mr.getAlert().setStatus("RESOLVED");
                mr.getAlert().setResolvedAt(Instant.now());
                mr.getAlert().setResolutionNotes("Resolved via maintenance work order " + mr.getRequestNumber() + ": " + mr.getResolutionSummary());
                alertRepository.save(mr.getAlert());
            }
        } else if ("CANCELLED".equalsIgnoreCase(targetStatus)) {
            if (mr.getResolutionSummary() == null || mr.getResolutionSummary().isBlank()) {
                throw new BusinessValidationException("Cancellation requires resolution summary notes stating reason.");
            }
        }

        MaintenanceRequest updated = maintenanceRepository.save(mr);
        return mapper.toMaintenanceResponse(updated);
    }

    @Override
    public PageResponse<MaintenanceRequestResponse> getAllRequests(String status, String priority, Long technicianId, Pageable pageable) {
        Specification<MaintenanceRequest> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("priority")), priority.trim().toUpperCase()));
            }
            if (technicianId != null) {
                predicates.add(cb.equal(root.get("assignedTechnician").get("id"), technicianId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<MaintenanceRequest> page = maintenanceRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toMaintenanceResponse));
    }

    @Override
    public MaintenanceRequestResponse getRequestById(Long id) {
        MaintenanceRequest mr = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "id", id));
        return mapper.toMaintenanceResponse(mr);
    }

    @Override
    public MaintenanceRequestResponse getRequestByNumber(String requestNumber) {
        MaintenanceRequest mr = maintenanceRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "requestNumber", requestNumber));
        return mapper.toMaintenanceResponse(mr);
    }
}
