package com.veloia.smartwater.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerRequest {

    @NotBlank(message = "Customer number is required")
    @Size(min = 3, max = 32, message = "Customer number must be between 3 and 32 characters")
    private String customerNumber;

    @NotBlank(message = "Customer type is required")
    @Pattern(regexp = "^(?i)(RESIDENTIAL|COMMERCIAL|INDUSTRIAL|INDIVIDUAL|CORPORATE|GOVERNMENT)$",
            message = "Customer type must be RESIDENTIAL, COMMERCIAL, INDUSTRIAL, INDIVIDUAL, CORPORATE, or GOVERNMENT")
    private String customerType;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 128, message = "Full name must be between 2 and 128 characters")
    private String fullName;

    @Email(message = "Invalid email address format")
    private String email;

    @Size(max = 32, message = "Phone number cannot exceed 32 characters")
    private String phoneNumber;

    @Size(max = 64, message = "Tax ID cannot exceed 64 characters")
    private String taxId;

    @NotBlank(message = "Billing address is required")
    @Size(max = 255, message = "Billing address cannot exceed 255 characters")
    private String billingAddress;

    @Pattern(regexp = "^(?i)(ACTIVE|INACTIVE|SUSPENDED|TERMINATED)$",
            message = "Status must be ACTIVE, INACTIVE, SUSPENDED, or TERMINATED")
    private String status = "ACTIVE";

    public CustomerRequest() {
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
