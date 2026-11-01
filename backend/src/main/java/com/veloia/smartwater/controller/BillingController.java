package com.veloia.smartwater.controller;

import com.veloia.smartwater.dto.billing.GenerateBillRequest;
import com.veloia.smartwater.dto.billing.PaymentResponse;
import com.veloia.smartwater.dto.billing.RecordPaymentRequest;
import com.veloia.smartwater.dto.billing.WaterBillResponse;
import com.veloia.smartwater.dto.common.ApiResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Billing & Payments", description = "Simulated utility billing, tariff rate calculation, and payment transactions")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/bills/generate")
    @Operation(summary = "Generate a water consumption bill for a customer and billing period")
    public ResponseEntity<ApiResponse<WaterBillResponse>> generateBill(@Valid @RequestBody GenerateBillRequest request) {
        WaterBillResponse bill = billingService.generateBill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Water bill generated successfully", bill));
    }

    @PostMapping("/bills/payments")
    @Operation(summary = "Record a full or partial customer payment against a bill")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(@Valid @RequestBody RecordPaymentRequest request) {
        PaymentResponse payment = billingService.recordPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", payment));
    }

    @GetMapping("/bills")
    @Operation(summary = "Get paginated water bills with customer and status filters")
    public ResponseEntity<ApiResponse<PageResponse<WaterBillResponse>>> getAllBills(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "billingPeriodStart", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<WaterBillResponse> page = billingService.getAllBills(customerId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/bills/{id}")
    @Operation(summary = "Get water bill details and outstanding balance by ID")
    public ResponseEntity<ApiResponse<WaterBillResponse>> getBillById(@PathVariable Long id) {
        WaterBillResponse bill = billingService.getBillById(id);
        return ResponseEntity.ok(ApiResponse.success(bill));
    }

    @GetMapping("/bills/{id}/payments")
    @Operation(summary = "Get payment transaction history for a specific bill")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsForBill(@PathVariable Long id) {
        List<PaymentResponse> list = billingService.getPaymentsForBill(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get paginated payment receipts across the utility")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) Long billId,
            @PageableDefault(size = 10, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<PaymentResponse> page = billingService.getAllPayments(billId, pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }
}
