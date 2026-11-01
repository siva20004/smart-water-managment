package com.veloia.smartwater.dto.reading;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public class MeterReadingRequest {

    @NotNull(message = "Meter ID is required")
    private Long meterId;

    @NotNull(message = "Reading timestamp is required")
    private Instant readingTimestamp;

    @NotNull(message = "Cumulative reading (m3) is required")
    @DecimalMin(value = "0.000", message = "Cumulative reading cannot be negative")
    private BigDecimal cumulativeReadingM3;

    @DecimalMin(value = "0.00", message = "Flow rate cannot be negative")
    private BigDecimal waterFlowRateLpm;

    @DecimalMin(value = "0.00", message = "Pressure cannot be negative")
    @DecimalMax(value = "25.00", message = "Pressure cannot exceed 25 bar")
    private BigDecimal waterPressureBar;

    @DecimalMin(value = "-10.0", message = "Temperature cannot be below -10 C")
    @DecimalMax(value = "60.0", message = "Temperature cannot exceed 60 C")
    private BigDecimal waterTemperatureCelsius;

    @Pattern(regexp = "^(?i)(AMI_AUTOMATED|AMR_DRIVE_BY|MANUAL_INSPECTION|ESTIMATED)$",
            message = "Reading source must be AMI_AUTOMATED, AMR_DRIVE_BY, MANUAL_INSPECTION, or ESTIMATED")
    private String readingSource = "AMI_AUTOMATED";

    private boolean meterReset = false;
    private String validationNotes;

    public MeterReadingRequest() {
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
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

    public boolean isMeterReset() {
        return meterReset;
    }

    public void setMeterReset(boolean meterReset) {
        this.meterReset = meterReset;
    }

    public String getValidationNotes() {
        return validationNotes;
    }

    public void setValidationNotes(String validationNotes) {
        this.validationNotes = validationNotes;
    }
}
