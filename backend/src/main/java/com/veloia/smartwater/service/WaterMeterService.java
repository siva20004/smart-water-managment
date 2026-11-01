package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.meter.WaterMeterRequest;
import com.veloia.smartwater.dto.meter.WaterMeterResponse;
import org.springframework.data.domain.Pageable;

public interface WaterMeterService {

    WaterMeterResponse createMeter(WaterMeterRequest request);

    WaterMeterResponse getMeterById(Long id);

    WaterMeterResponse getMeterBySerial(String serialNumber);

    PageResponse<WaterMeterResponse> getAllMeters(String search, String status, Long customerId, Long locationId, Pageable pageable);

    WaterMeterResponse updateMeter(Long id, WaterMeterRequest request);

    WaterMeterResponse assignMeterToCustomer(Long meterId, Long customerId);

    WaterMeterResponse setMeterStatus(Long meterId, String status);

    void deleteMeter(Long meterId);
}
