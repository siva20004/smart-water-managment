package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.billing.PaymentResponse;
import com.veloia.smartwater.dto.billing.RecordPaymentRequest;
import com.veloia.smartwater.entity.Customer;
import com.veloia.smartwater.entity.Payment;
import com.veloia.smartwater.entity.WaterBill;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.CustomerRepository;
import com.veloia.smartwater.repository.PaymentRepository;
import com.veloia.smartwater.repository.WaterBillRepository;
import com.veloia.smartwater.repository.WaterMeterRepository;
import com.veloia.smartwater.service.impl.BillingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private WaterBillRepository billRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private WaterMeterRepository meterRepository;

    private final EntityDtoMapper mapper = new EntityDtoMapper();

    private BillingServiceImpl billingService;

    private WaterBill sampleBill;

    @BeforeEach
    void setUp() {
        billingService = new BillingServiceImpl(billRepository, paymentRepository, customerRepository, meterRepository, mapper);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setCustomerNumber("CUST-001");
        customer.setFullName("Tanaka Taro");

        sampleBill = new WaterBill();
        sampleBill.setId(10L);
        sampleBill.setBillNumber("BILL-202610-001");
        sampleBill.setCustomer(customer);
        sampleBill.setBillingPeriodStart(LocalDate.of(2026, 10, 1));
        sampleBill.setBillingPeriodEnd(LocalDate.of(2026, 10, 31));
        sampleBill.setBaseFee(new BigDecimal("1500.00"));
        sampleBill.setVolumetricRate(new BigDecimal("180.00"));
        sampleBill.setConsumptionM3(new BigDecimal("20.000"));
        sampleBill.setConsumptionCharge(new BigDecimal("3600.00"));
        sampleBill.setTaxAmount(new BigDecimal("510.00"));
        sampleBill.setTotalAmount(new BigDecimal("5610.00"));
        sampleBill.setDueDate(LocalDate.of(2026, 11, 20));
        sampleBill.setStatus("ISSUED");
        sampleBill.setPayments(new ArrayList<>());
    }

    @Test
    @DisplayName("recordPayment records partial payment and transitions bill to PARTIALLY_PAID")
    void recordPayment_Partial_Success() {
        when(billRepository.findById(10L)).thenReturn(Optional.of(sampleBill));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        RecordPaymentRequest request = new RecordPaymentRequest();
        request.setBillId(10L);
        request.setAmount(new BigDecimal("2000.00"));
        request.setPaymentMethod("CREDIT_CARD");

        PaymentResponse response = billingService.recordPayment(request);

        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo("2000.00");
        assertThat(sampleBill.getStatus()).isEqualTo("PARTIALLY_PAID");
        verify(billRepository).save(sampleBill);
    }

    @Test
    @DisplayName("recordPayment records full payment and transitions bill to PAID")
    void recordPayment_Full_Success() {
        when(billRepository.findById(10L)).thenReturn(Optional.of(sampleBill));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(101L);
            return p;
        });

        RecordPaymentRequest request = new RecordPaymentRequest();
        request.setBillId(10L);
        request.setAmount(new BigDecimal("5610.00"));
        request.setPaymentMethod("BANK_TRANSFER");

        PaymentResponse response = billingService.recordPayment(request);

        assertThat(response).isNotNull();
        assertThat(sampleBill.getStatus()).isEqualTo("PAID");
        verify(billRepository).save(sampleBill);
    }

    @Test
    @DisplayName("recordPayment rejects payment exceeding outstanding balance")
    void recordPayment_ExceedingAmount_ThrowsException() {
        when(billRepository.findById(10L)).thenReturn(Optional.of(sampleBill));

        RecordPaymentRequest request = new RecordPaymentRequest();
        request.setBillId(10L);
        request.setAmount(new BigDecimal("6000.00")); // exceeds 5610.00
        request.setPaymentMethod("CREDIT_CARD");

        assertThatThrownBy(() -> billingService.recordPayment(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("exceeds the outstanding balance");

        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("recordPayment rejects payment when bill is already PAID")
    void recordPayment_AlreadyPaid_ThrowsException() {
        sampleBill.setStatus("PAID");
        when(billRepository.findById(10L)).thenReturn(Optional.of(sampleBill));

        RecordPaymentRequest request = new RecordPaymentRequest();
        request.setBillId(10L);
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMethod("CREDIT_CARD");

        assertThatThrownBy(() -> billingService.recordPayment(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("already marked as PAID");
    }
}
