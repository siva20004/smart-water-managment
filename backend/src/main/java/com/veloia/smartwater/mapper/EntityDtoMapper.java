package com.veloia.smartwater.mapper;

import com.veloia.smartwater.dto.alert.LeakAlertResponse;
import com.veloia.smartwater.dto.billing.PaymentResponse;
import com.veloia.smartwater.dto.billing.WaterBillResponse;
import com.veloia.smartwater.dto.customer.CustomerRequest;
import com.veloia.smartwater.dto.customer.CustomerResponse;
import com.veloia.smartwater.dto.maintenance.MaintenanceRequestResponse;
import com.veloia.smartwater.dto.meter.WaterMeterResponse;
import com.veloia.smartwater.dto.quality.WaterQualityResponse;
import com.veloia.smartwater.dto.reading.MeterReadingResponse;
import com.veloia.smartwater.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityDtoMapper {

    public Customer toCustomerEntity(CustomerRequest request) {
        if (request == null) return null;
        Customer c = new Customer();
        c.setCustomerNumber(request.getCustomerNumber());
        c.setCustomerType(request.getCustomerType().toUpperCase());
        c.setFullName(request.getFullName());
        c.setEmail(request.getEmail());
        c.setPhoneNumber(request.getPhoneNumber());
        c.setTaxId(request.getTaxId());
        c.setBillingAddress(request.getBillingAddress());
        c.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE");
        return c;
    }

    public void updateCustomerEntity(Customer c, CustomerRequest request) {
        if (c == null || request == null) return;
        c.setCustomerType(request.getCustomerType().toUpperCase());
        c.setFullName(request.getFullName());
        c.setEmail(request.getEmail());
        c.setPhoneNumber(request.getPhoneNumber());
        c.setTaxId(request.getTaxId());
        c.setBillingAddress(request.getBillingAddress());
        if (request.getStatus() != null) {
            c.setStatus(request.getStatus().toUpperCase());
        }
    }

    public CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        CustomerResponse dto = new CustomerResponse();
        dto.setId(customer.getId());
        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setCustomerType(customer.getCustomerType());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setTaxId(customer.getTaxId());
        dto.setBillingAddress(customer.getBillingAddress());
        dto.setStatus(customer.getStatus());
        dto.setMeterCount(customer.getMeters() != null ? customer.getMeters().size() : 0);
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }

    public WaterMeterResponse toMeterResponse(WaterMeter meter) {
        if (meter == null) return null;
        WaterMeterResponse dto = new WaterMeterResponse();
        dto.setId(meter.getId());
        dto.setSerialNumber(meter.getSerialNumber());
        if (meter.getCustomer() != null) {
            dto.setCustomerId(meter.getCustomer().getId());
            dto.setCustomerName(meter.getCustomer().getFullName());
        }
        if (meter.getLocation() != null) {
            dto.setLocationId(meter.getLocation().getId());
            dto.setLocationCode(meter.getLocation().getLocationCode());
        }
        dto.setMeterModel(meter.getMeterModel());
        dto.setMeterType(meter.getMeterType());
        dto.setDiameterMm(meter.getDiameterMm());
        dto.setInstallationDate(meter.getInstallationDate());
        dto.setStatus(meter.getStatus());
        dto.setLastInspectionDate(meter.getLastInspectionDate());
        dto.setBatteryLevelPercent(meter.getBatteryLevelPercent());
        dto.setLastCommunicationAt(meter.getLastCommunicationAt() != null ? meter.getLastCommunicationAt() : meter.getUpdatedAt());
        return dto;
    }

    public MeterReadingResponse toReadingResponse(MeterReading reading) {
        if (reading == null) return null;
        MeterReadingResponse dto = new MeterReadingResponse();
        dto.setId(reading.getId());
        if (reading.getMeter() != null) {
            dto.setMeterId(reading.getMeter().getId());
            dto.setMeterSerialNumber(reading.getMeter().getSerialNumber());
        }
        dto.setReadingTimestamp(reading.getReadingTimestamp());
        dto.setCumulativeReadingM3(reading.getCumulativeReadingM3());
        dto.setIncrementalConsumptionM3(reading.getIncrementalConsumptionM3());
        dto.setWaterFlowRateLpm(reading.getWaterFlowRateLpm());
        dto.setWaterPressureBar(reading.getWaterPressureBar());
        dto.setWaterTemperatureCelsius(reading.getWaterTemperatureCelsius());
        dto.setReadingSource(reading.getReadingSource());
        dto.setIsValidated(reading.getIsValidated());
        dto.setValidationNotes(reading.getValidationNotes());
        return dto;
    }

    public WaterBillResponse toBillResponse(WaterBill bill) {
        if (bill == null) return null;
        WaterBillResponse dto = new WaterBillResponse();
        dto.setId(bill.getId());
        dto.setBillNumber(bill.getBillNumber());
        if (bill.getCustomer() != null) {
            dto.setCustomerId(bill.getCustomer().getId());
            dto.setCustomerName(bill.getCustomer().getFullName());
        }
        if (bill.getMeter() != null) {
            dto.setMeterId(bill.getMeter().getId());
            dto.setMeterSerial(bill.getMeter().getSerialNumber());
        }
        dto.setBillingPeriodStart(bill.getBillingPeriodStart());
        dto.setBillingPeriodEnd(bill.getBillingPeriodEnd());
        dto.setBaseFee(bill.getBaseFee());
        dto.setVolumetricRate(bill.getVolumetricRate());
        dto.setConsumptionM3(bill.getConsumptionM3());
        dto.setConsumptionCharge(bill.getConsumptionCharge());
        dto.setTaxAmount(bill.getTaxAmount());
        dto.setTotalAmount(bill.getTotalAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setStatus(bill.getStatus());

        java.math.BigDecimal paid = java.math.BigDecimal.ZERO;
        if (bill.getPayments() != null) {
            paid = bill.getPayments().stream()
                    .filter(p -> "COMPLETED".equalsIgnoreCase(p.getTransactionStatus()))
                    .map(Payment::getAmount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }
        dto.setPaidAmount(paid);
        dto.setOutstandingBalance(bill.getTotalAmount().subtract(paid).max(java.math.BigDecimal.ZERO));
        return dto;
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) return null;
        PaymentResponse dto = new PaymentResponse();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        if (payment.getBill() != null) {
            dto.setBillId(payment.getBill().getId());
            dto.setBillNumber(payment.getBill().getBillNumber());
        }
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionStatus(payment.getTransactionStatus());
        dto.setNotes(payment.getNotes());
        return dto;
    }

    public WaterQualityResponse toQualityResponse(WaterQualityRecord record) {
        if (record == null) return null;
        WaterQualityResponse dto = new WaterQualityResponse();
        dto.setId(record.getId());
        dto.setSampleCode(record.getSampleCode());
        if (record.getLocation() != null) {
            dto.setLocationId(record.getLocation().getId());
            dto.setLocationCode(record.getLocation().getLocationCode());
        }
        dto.setSampleTimestamp(record.getSampleTimestamp());
        dto.setPhLevel(record.getPhLevel());
        dto.setTurbidityNtu(record.getTurbidityNtu());
        dto.setResidualChlorineMgL(record.getResidualChlorineMgL());
        dto.setDissolvedOxygenMgL(record.getDissolvedOxygenMgL());
        dto.setConductivityUsCm(record.getConductivityUsCm());
        dto.setWaterTemperatureCelsius(record.getWaterTemperatureCelsius());
        dto.setPotabilityStatus(record.getPotabilityStatus());
        dto.setSampledBy(record.getSampledBy());
        return dto;
    }

    public LeakAlertResponse toAlertResponse(LeakAlert alert) {
        if (alert == null) return null;
        LeakAlertResponse dto = new LeakAlertResponse();
        dto.setId(alert.getId());
        dto.setAlertCode(alert.getAlertCode());
        if (alert.getMeter() != null) {
            dto.setMeterId(alert.getMeter().getId());
            dto.setMeterSerial(alert.getMeter().getSerialNumber());
        }
        if (alert.getLocation() != null) {
            dto.setLocationId(alert.getLocation().getId());
            dto.setLocationCode(alert.getLocation().getLocationCode());
        }
        dto.setSeverity(alert.getSeverity());
        dto.setAlertType(alert.getAlertType());
        dto.setDetectedAt(alert.getDetectedAt());
        dto.setEstimatedLossRateM3H(alert.getEstimatedLossRateM3H());
        dto.setStatus(alert.getStatus());
        dto.setResolutionNotes(alert.getResolutionNotes());
        dto.setResolvedAt(alert.getResolvedAt());
        return dto;
    }

    public MaintenanceRequestResponse toMaintenanceResponse(MaintenanceRequest mr) {
        if (mr == null) return null;
        MaintenanceRequestResponse dto = new MaintenanceRequestResponse();
        dto.setId(mr.getId());
        dto.setRequestNumber(mr.getRequestNumber());
        if (mr.getMeter() != null) {
            dto.setMeterId(mr.getMeter().getId());
            dto.setMeterSerial(mr.getMeter().getSerialNumber());
        }
        if (mr.getAlert() != null) {
            dto.setAlertId(mr.getAlert().getId());
            dto.setAlertCode(mr.getAlert().getAlertCode());
        }
        if (mr.getLocation() != null) {
            dto.setLocationId(mr.getLocation().getId());
            dto.setLocationCode(mr.getLocation().getLocationCode());
        }
        if (mr.getAssignedTechnician() != null) {
            dto.setAssignedTechnicianId(mr.getAssignedTechnician().getId());
            dto.setAssignedTechnicianName(mr.getAssignedTechnician().getFullName());
        }
        dto.setPriority(mr.getPriority());
        dto.setCategory(mr.getCategory());
        dto.setTitle(mr.getTitle());
        dto.setDescription(mr.getDescription());
        dto.setStatus(mr.getStatus());
        dto.setScheduledStart(mr.getScheduledStart());
        dto.setActualCompletion(mr.getActualCompletion());
        dto.setResolutionSummary(mr.getResolutionSummary());
        dto.setPartsCost(mr.getPartsCost());
        dto.setLaborHours(mr.getLaborHours());
        return dto;
    }
}
