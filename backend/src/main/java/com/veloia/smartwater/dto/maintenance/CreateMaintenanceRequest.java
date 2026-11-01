package com.veloia.smartwater.dto.maintenance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public class CreateMaintenanceRequest {

    private Long meterId;
    private Long alertId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    private Long assignedTechnicianId;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(?i)(LOW|MEDIUM|HIGH|EMERGENCY)$", message = "Priority must be LOW, MEDIUM, HIGH, or EMERGENCY")
    private String priority = "MEDIUM";

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^(?i)(LEAK_REPAIR|METER_REPLACEMENT|CALIBRATION|PRESSURE_CHECK|WATER_QUALITY_RETEST|ROUTINE_INSPECTION)$",
            message = "Category must be LEAK_REPAIR, METER_REPLACEMENT, CALIBRATION, PRESSURE_CHECK, WATER_QUALITY_RETEST, or ROUTINE_INSPECTION")
    private String category;

    @NotBlank(message = "Work order title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Work order description is required")
    private String description;

    private Instant scheduledStart;

    public CreateMaintenanceRequest() {
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(Long assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(Instant scheduledStart) {
        this.scheduledStart = scheduledStart;
    }
}
