package com.veloia.smartwater.dto.quality;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public class WaterQualityRequest {

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Sample timestamp is required")
    private Instant sampleTimestamp;

    @NotNull(message = "pH level is required")
    @DecimalMin(value = "0.00", message = "pH cannot be below 0.00")
    @DecimalMax(value = "14.00", message = "pH cannot exceed 14.00")
    private BigDecimal phLevel;

    @NotNull(message = "Turbidity (NTU) is required")
    @DecimalMin(value = "0.000", message = "Turbidity cannot be negative")
    private BigDecimal turbidityNtu;

    @NotNull(message = "Residual chlorine (mg/L) is required")
    @DecimalMin(value = "0.000", message = "Residual chlorine cannot be negative")
    private BigDecimal residualChlorineMgL;

    @DecimalMin(value = "0.00", message = "Dissolved oxygen cannot be negative")
    private BigDecimal dissolvedOxygenMgL;

    @DecimalMin(value = "0.00", message = "Conductivity cannot be negative")
    private BigDecimal conductivityUsCm;

    @DecimalMin(value = "-5.0", message = "Temperature cannot be below -5 C")
    @DecimalMax(value = "50.0", message = "Temperature cannot exceed 50 C")
    private BigDecimal waterTemperatureCelsius;

    @NotBlank(message = "Sampled by technician/chemist name is required")
    @Size(max = 128, message = "Sampled by cannot exceed 128 characters")
    private String sampledBy;

    public WaterQualityRequest() {
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

    public String getSampledBy() {
        return sampledBy;
    }

    public void setSampledBy(String sampledBy) {
        this.sampledBy = sampledBy;
    }
}
