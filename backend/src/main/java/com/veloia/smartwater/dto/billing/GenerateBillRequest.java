package com.veloia.smartwater.dto.billing;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GenerateBillRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long meterId;

    @NotNull(message = "Billing period start date is required")
    private LocalDate billingPeriodStart;

    @NotNull(message = "Billing period end date is required")
    private LocalDate billingPeriodEnd;

    private BigDecimal simulatedBaseFee;
    private BigDecimal simulatedVolumetricRate;
    private BigDecimal taxRatePercent;
    private LocalDate dueDate;

    public GenerateBillRequest() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public BigDecimal getSimulatedBaseFee() {
        return simulatedBaseFee;
    }

    public void setSimulatedBaseFee(BigDecimal simulatedBaseFee) {
        this.simulatedBaseFee = simulatedBaseFee;
    }

    public BigDecimal getSimulatedVolumetricRate() {
        return simulatedVolumetricRate;
    }

    public void setSimulatedVolumetricRate(BigDecimal simulatedVolumetricRate) {
        this.simulatedVolumetricRate = simulatedVolumetricRate;
    }

    public BigDecimal getTaxRatePercent() {
        return taxRatePercent;
    }

    public void setTaxRatePercent(BigDecimal taxRatePercent) {
        this.taxRatePercent = taxRatePercent;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
