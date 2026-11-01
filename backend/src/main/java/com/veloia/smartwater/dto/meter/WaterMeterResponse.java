package com.veloia.smartwater.dto.meter;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WaterMeterResponse {
    private Long id;
    private String serialNumber;
    private Long customerId;
    private String customerName;
    private Long locationId;
    private String locationCode;
    private String meterModel;
    private String meterType;
    private Integer diameterMm;
    private LocalDate installationDate;
    private String status;
    private LocalDate lastInspectionDate;
    private BigDecimal batteryLevelPercent;
    private java.time.Instant lastCommunicationAt;

    public WaterMeterResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
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

    public java.time.Instant getLastCommunicationAt() {
        return lastCommunicationAt;
    }

    public void setLastCommunicationAt(java.time.Instant lastCommunicationAt) {
        this.lastCommunicationAt = lastCommunicationAt;
    }
}
