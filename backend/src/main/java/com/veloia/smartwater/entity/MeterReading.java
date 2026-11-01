package com.veloia.smartwater.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "meter_readings", uniqueConstraints = {
    @UniqueConstraint(name = "uq_meter_reading_timestamp", columnNames = {"meter_id", "reading_timestamp"})
})
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", nullable = false)
    private WaterMeter meter;

    @Column(name = "reading_timestamp", nullable = false)
    private Instant readingTimestamp;

    @Column(name = "cumulative_reading_m3", nullable = false, precision = 12, scale = 3)
    private BigDecimal cumulativeReadingM3;

    @Column(name = "incremental_consumption_m3", nullable = false, precision = 10, scale = 3)
    private BigDecimal incrementalConsumptionM3 = BigDecimal.ZERO;

    @Column(name = "water_flow_rate_lpm", precision = 8, scale = 2)
    private BigDecimal waterFlowRateLpm;

    @Column(name = "water_pressure_bar", precision = 5, scale = 2)
    private BigDecimal waterPressureBar;

    @Column(name = "water_temperature_celsius", precision = 4, scale = 1)
    private BigDecimal waterTemperatureCelsius;

    @Column(name = "reading_source", nullable = false, length = 32)
    private String readingSource = "AMI_AUTOMATED";

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated = true;

    @Column(name = "validation_notes", columnDefinition = "TEXT")
    private String validationNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public MeterReading() {
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

    public WaterMeter getMeter() {
        return meter;
    }

    public void setMeter(WaterMeter meter) {
        this.meter = meter;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
