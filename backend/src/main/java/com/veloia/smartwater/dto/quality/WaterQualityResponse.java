package com.veloia.smartwater.dto.quality;

import java.math.BigDecimal;
import java.time.Instant;

public class WaterQualityResponse {
    private Long id;
    private String sampleCode;
    private Long locationId;
    private String locationCode;
    private Instant sampleTimestamp;
    private BigDecimal phLevel;
    private BigDecimal turbidityNtu;
    private BigDecimal residualChlorineMgL;
    private BigDecimal dissolvedOxygenMgL;
    private BigDecimal conductivityUsCm;
    private BigDecimal waterTemperatureCelsius;
    private String potabilityStatus;
    private String sampledBy;

    public WaterQualityResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
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

    public Instant getSampleTimestamp() {
        return sampleTimestamp;
    }

    public void setSampleTimestamp(Instant sampleTimestamp) {
        this.sampleTimestamp = sampleTimestamp;
    }

    public BigDecimal getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(BigDecimal phLevel) {
        this.phLevel = phLevel;
    }

    public BigDecimal getTurbidityNtu() {
        return turbidityNtu;
    }

    public void setTurbidityNtu(BigDecimal turbidityNtu) {
        this.turbidityNtu = turbidityNtu;
    }

    public BigDecimal getResidualChlorineMgL() {
        return residualChlorineMgL;
    }

    public void setResidualChlorineMgL(BigDecimal residualChlorineMgL) {
        this.residualChlorineMgL = residualChlorineMgL;
    }

    public BigDecimal getDissolvedOxygenMgL() {
        return dissolvedOxygenMgL;
    }

    public void setDissolvedOxygenMgL(BigDecimal dissolvedOxygenMgL) {
        this.dissolvedOxygenMgL = dissolvedOxygenMgL;
    }

    public BigDecimal getConductivityUsCm() {
        return conductivityUsCm;
    }

    public void setConductivityUsCm(BigDecimal conductivityUsCm) {
        this.conductivityUsCm = conductivityUsCm;
    }

    public BigDecimal getWaterTemperatureCelsius() {
        return waterTemperatureCelsius;
    }

    public void setWaterTemperatureCelsius(BigDecimal waterTemperatureCelsius) {
        this.waterTemperatureCelsius = waterTemperatureCelsius;
    }

    public String getPotabilityStatus() {
        return potabilityStatus;
    }

    public void setPotabilityStatus(String potabilityStatus) {
        this.potabilityStatus = potabilityStatus;
    }

    public String getSampledBy() {
        return sampledBy;
    }

    public void setSampledBy(String sampledBy) {
        this.sampledBy = sampledBy;
    }
}
