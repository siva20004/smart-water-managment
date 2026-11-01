package com.veloia.smartwater.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "water_quality_records")
public class WaterQualityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sample_code", nullable = false, unique = true, length = 64)
    private String sampleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "sample_timestamp", nullable = false)
    private Instant sampleTimestamp;

    @Column(name = "ph_level", nullable = false, precision = 4, scale = 2)
    private BigDecimal phLevel;

    @Column(name = "turbidity_ntu", nullable = false, precision = 6, scale = 3)
    private BigDecimal turbidityNtu;

    @Column(name = "residual_chlorine_mg_l", nullable = false, precision = 5, scale = 3)
    private BigDecimal residualChlorineMgL;

    @Column(name = "dissolved_oxygen_mg_l", precision = 5, scale = 2)
    private BigDecimal dissolvedOxygenMgL;

    @Column(name = "conductivity_us_cm", precision = 7, scale = 2)
    private BigDecimal conductivityUsCm;

    @Column(name = "water_temperature_celsius", precision = 4, scale = 1)
    private BigDecimal waterTemperatureCelsius;

    @Column(name = "potability_status", nullable = false, length = 32)
    private String potabilityStatus = "POTABLE";

    @Column(name = "sampled_by", nullable = false, length = 128)
    private String sampledBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public WaterQualityRecord() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
