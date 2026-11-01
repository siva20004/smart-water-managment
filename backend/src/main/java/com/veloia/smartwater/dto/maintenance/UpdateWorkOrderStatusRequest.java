package com.veloia.smartwater.dto.maintenance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public class UpdateWorkOrderStatusRequest {

    @NotBlank(message = "Target status is required")
    @Pattern(regexp = "^(?i)(PENDING|OPEN|ASSIGNED|IN_PROGRESS|COMPLETED|CANCELLED)$",
            message = "Status must be PENDING, OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, or CANCELLED")
    private String status;

    private Long assignedTechnicianId;
    private String resolutionSummary;

    @DecimalMin(value = "0.00", message = "Parts cost cannot be negative")
    private BigDecimal partsCost;

    @DecimalMin(value = "0.00", message = "Labor hours cannot be negative")
    private BigDecimal laborHours;

    public UpdateWorkOrderStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(Long assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public String getResolutionSummary() {
        return resolutionSummary;
    }

    public void setResolutionSummary(String resolutionSummary) {
        this.resolutionSummary = resolutionSummary;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(BigDecimal partsCost) {
        this.partsCost = partsCost;
    }

    public BigDecimal getLaborHours() {
        return laborHours;
    }

    public void setLaborHours(BigDecimal laborHours) {
        this.laborHours = laborHours;
    }
}
