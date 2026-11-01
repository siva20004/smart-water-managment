package com.veloia.smartwater.service;

import com.veloia.smartwater.dto.billing.GenerateBillRequest;
import com.veloia.smartwater.dto.billing.PaymentResponse;
import com.veloia.smartwater.dto.billing.RecordPaymentRequest;
import com.veloia.smartwater.dto.billing.WaterBillResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingService {

    WaterBillResponse generateBill(GenerateBillRequest request);

    PaymentResponse recordPayment(RecordPaymentRequest request);

    WaterBillResponse getBillById(Long id);

    WaterBillResponse getBillByNumber(String billNumber);

    PageResponse<WaterBillResponse> getAllBills(Long customerId, String status, Pageable pageable);

    PageResponse<PaymentResponse> getAllPayments(Long billId, Pageable pageable);

    List<PaymentResponse> getPaymentsForBill(Long billId);
}
