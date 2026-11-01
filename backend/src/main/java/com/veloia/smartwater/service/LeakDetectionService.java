package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.alert.LeakAlertResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LeakDetectionService {

    List<LeakAlertResponse> runDetectionScan();

    List<LeakAlertResponse> evaluateMeter(Long meterId);

    LeakAlertResponse updateAlertStatus(Long alertId, String status, String resolutionNotes);

    PageResponse<LeakAlertResponse> getAllAlerts(String status, String severity, Long locationId, Pageable pageable);

    LeakAlertResponse getAlertById(Long alertId);

    LeakAlertResponse getAlertByCode(String alertCode);
}
