package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.maintenance.CreateMaintenanceRequest;
import com.veloia.smartwater.dto.maintenance.MaintenanceRequestResponse;
import com.veloia.smartwater.dto.maintenance.UpdateWorkOrderStatusRequest;
import org.springframework.data.domain.Pageable;

public interface MaintenanceService {

    MaintenanceRequestResponse createRequest(CreateMaintenanceRequest request);

    MaintenanceRequestResponse updateStatus(Long id, UpdateWorkOrderStatusRequest request);

    PageResponse<MaintenanceRequestResponse> getAllRequests(String status, String priority, Long technicianId, Pageable pageable);

    MaintenanceRequestResponse getRequestById(Long id);

    MaintenanceRequestResponse getRequestByNumber(String requestNumber);
}
