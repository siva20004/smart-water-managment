package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.analytics.ConsumptionAnalyticsDtos.*;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.dto.reading.MeterReadingRequest;
import com.veloia.smartwater.dto.reading.MeterReadingResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MeterReadingService {

    MeterReadingResponse submitReading(MeterReadingRequest request);

    PageResponse<MeterReadingResponse> getReadingsByMeter(Long meterId, Pageable pageable);

    PageResponse<MeterReadingResponse> getAllReadings(Long meterId, Pageable pageable);

    List<DailyConsumption> getDailyConsumption(int days);

    List<MonthlyConsumption> getMonthlyConsumption();

    List<CustomerConsumption> getCustomerConsumption();

    List<RegionConsumption> getRegionConsumption();
}
