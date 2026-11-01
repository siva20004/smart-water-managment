package com.veloia.smartwater.dto.meter;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class WaterMeterRequest {

    @NotBlank(message = "Serial number is required")
    @Size(min = 3, max = 64, message = "Serial number must be between 3 and 64 characters")
    private String serialNumber;

    private Long customerId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotBlank(message = "Meter model is required")
    @Size(max = 64, message = "Meter model cannot exceed 64 characters")
    private String meterModel;

    @NotBlank(message = "Meter type is required")
    @Pattern(regexp = "^(?i)(ULTRASONIC|ELECTROMAGNETIC|MECHANICAL_PISTON|OPTICAL_DIGITAL)$",
            message = "Meter type must be ULTRASONIC, ELECTROMAGNETIC, MECHANICAL_PISTON, or OPTICAL_DIGITAL")
    private String meterType;

    @NotNull(message = "Diameter (mm) is required")
    @Min(value = 1, message = "Diameter must be at least 1 mm")
    @Max(value = 1000, message = "Diameter cannot exceed 1000 mm")
    private Integer diameterMm;

    @NotNull(message = "Installation date is required")
    private LocalDate installationDate;

    @Pattern(regexp = "^(?i)(ACTIVE|INACTIVE|FAULTY|MAINTENANCE|RETIRED|REMOVED)$",
            message = "Status must be ACTIVE, INACTIVE, FAULTY, MAINTENANCE, RETIRED, or REMOVED")
    private String status = "ACTIVE";

    private LocalDate lastInspectionDate;

    @DecimalMin(value = "0.00", message = "Battery level cannot be negative")
    @DecimalMax(value = "100.00", message = "Battery level cannot exceed 100%")
    private BigDecimal batteryLevelPercent;

    public WaterMeterRequest() {
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getMeterModel() {
        return meterModel;
    }

    public void setMeterModel(String meterModel) {
        this.meterModel = meterModel;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public Integer getDiameterMm() {
        return diameterMm;
    }

    public void setDiameterMm(Integer diameterMm) {
        this.diameterMm = diameterMm;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLastInspectionDate() {
        return lastInspectionDate;
    }

    public void setLastInspectionDate(LocalDate lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    public BigDecimal getBatteryLevelPercent() {
        return batteryLevelPercent;
    }

    public void setBatteryLevelPercent(BigDecimal batteryLevelPercent) {
        this.batteryLevelPercent = batteryLevelPercent;
    }
}
