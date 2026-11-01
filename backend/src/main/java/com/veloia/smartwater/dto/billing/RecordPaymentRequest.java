package com.veloia.smartwater.dto.billing;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RecordPaymentRequest {

    @NotNull(message = "Bill ID is required")
    private Long billId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(?i)(BANK_TRANSFER|DIRECT_DEBIT|CREDIT_CARD|CONVENIENCE_STORE_PAY|ELECTRONIC_WALLET)$",
            message = "Payment method must be BANK_TRANSFER, DIRECT_DEBIT, CREDIT_CARD, CONVENIENCE_STORE_PAY, or ELECTRONIC_WALLET")
    private String paymentMethod;

    private String notes;

    public RecordPaymentRequest() {
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
