package com.veloia.smartwater.dto.billing;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WaterBillResponse {
    private Long id;
    private String billNumber;
    private Long customerId;
    private String customerName;
    private Long meterId;
    private String meterSerial;
    private LocalDate billingPeriodStart;
    private LocalDate billingPeriodEnd;
    private BigDecimal baseFee;
    private BigDecimal volumetricRate;
    private BigDecimal consumptionM3;
    private BigDecimal consumptionCharge;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private LocalDate dueDate;
    private String status;
    private BigDecimal paidAmount;
    private BigDecimal outstandingBalance;

    public WaterBillResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
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

    public Long getMeterId() {
        return meterId;
    }

    public void setMeterId(Long meterId) {
        this.meterId = meterId;
    }

    public String getMeterSerial() {
        return meterSerial;
    }

    public void setMeterSerial(String meterSerial) {
        this.meterSerial = meterSerial;
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

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public BigDecimal getVolumetricRate() {
        return volumetricRate;
    }

    public void setVolumetricRate(BigDecimal volumetricRate) {
        this.volumetricRate = volumetricRate;
    }

    public BigDecimal getConsumptionM3() {
        return consumptionM3;
    }

    public void setConsumptionM3(BigDecimal consumptionM3) {
        this.consumptionM3 = consumptionM3;
    }

    public BigDecimal getConsumptionCharge() {
        return consumptionCharge;
    }

    public void setConsumptionCharge(BigDecimal consumptionCharge) {
        this.consumptionCharge = consumptionCharge;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(BigDecimal outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }
}
