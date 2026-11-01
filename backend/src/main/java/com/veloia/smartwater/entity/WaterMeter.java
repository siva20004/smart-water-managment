package com.veloia.smartwater.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "water_meters")
public class WaterMeter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true, length = 64)
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "meter_model", nullable = false, length = 64)
    private String meterModel;

    @Column(name = "meter_type", nullable = false, length = 32)
    private String meterType;

    @Column(name = "diameter_mm", nullable = false)
    private Integer diameterMm;

    @Column(name = "installation_date", nullable = false)
    private LocalDate installationDate;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "last_inspection_date")
    private LocalDate lastInspectionDate;

    @Column(name = "last_communication_at")
    private Instant lastCommunicationAt;

    @Column(name = "battery_level_percent", precision = 5, scale = 2)
    private BigDecimal batteryLevelPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "meter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MeterReading> readings = new ArrayList<>();

    public WaterMeter() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Instant getLastCommunicationAt() {
        return lastCommunicationAt;
    }

    public void setLastCommunicationAt(Instant lastCommunicationAt) {
        this.lastCommunicationAt = lastCommunicationAt;
    }

    public BigDecimal getBatteryLevelPercent() {
        return batteryLevelPercent;
    }

    public void setBatteryLevelPercent(BigDecimal batteryLevelPercent) {
        this.batteryLevelPercent = batteryLevelPercent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MeterReading> getReadings() {
        return readings;
    }

    public void setReadings(List<MeterReading> readings) {
        this.readings = readings;
    }
}
