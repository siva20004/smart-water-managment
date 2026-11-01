package com.veloia.smartwater.dto.reading;

import java.math.BigDecimal;
import java.time.Instant;

public class MeterReadingResponse {
    private Long id;
    private Long meterId;
    private String meterSerialNumber;
    private Instant readingTimestamp;
    private BigDecimal cumulativeReadingM3;
    private BigDecimal incrementalConsumptionM3;
    private BigDecimal waterFlowRateLpm;
    private BigDecimal waterPressureBar;
    private BigDecimal waterTemperatureCelsius;
    private String readingSource;
    private Boolean isValidated;
    private String validationNotes;

    public MeterReadingResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    public void setMeterSerialNumber(String meterSerialNumber) {
        this.meterSerialNumber = meterSerialNumber;
    }

    public Instant getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(Instant readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public BigDecimal getCumulativeReadingM3() {
        return cumulativeReadingM3;
    }

    public void setCumulativeReadingM3(BigDecimal cumulativeReadingM3) {
        this.cumulativeReadingM3 = cumulativeReadingM3;
    }

    public BigDecimal getIncrementalConsumptionM3() {
        return incrementalConsumptionM3;
    }

    public void setIncrementalConsumptionM3(BigDecimal incrementalConsumptionM3) {
        this.incrementalConsumptionM3 = incrementalConsumptionM3;
    }

    public BigDecimal getWaterFlowRateLpm() {
        return waterFlowRateLpm;
    }

    public void setWaterFlowRateLpm(BigDecimal waterFlowRateLpm) {
        this.waterFlowRateLpm = waterFlowRateLpm;
    }

    public BigDecimal getWaterPressureBar() {
        return waterPressureBar;
    }

    public void setWaterPressureBar(BigDecimal waterPressureBar) {
        this.waterPressureBar = waterPressureBar;
    }

    public BigDecimal getWaterTemperatureCelsius() {
        return waterTemperatureCelsius;
    }

    public void setWaterTemperatureCelsius(BigDecimal waterTemperatureCelsius) {
        this.waterTemperatureCelsius = waterTemperatureCelsius;
    }

    public String getReadingSource() {
        return readingSource;
    }

    public void setReadingSource(String readingSource) {
        this.readingSource = readingSource;
    }

    public Boolean getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(Boolean validated) {
        isValidated = validated;
    }

    public String getValidationNotes() {
        return validationNotes;
    }

    public void setValidationNotes(String validationNotes) {
        this.validationNotes = validationNotes;
    }
}
