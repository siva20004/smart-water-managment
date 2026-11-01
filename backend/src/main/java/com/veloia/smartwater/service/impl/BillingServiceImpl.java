package com.veloia.smartwater.service.impl;

import com.veloia.smartwater.dto.billing.GenerateBillRequest;
import com.veloia.smartwater.dto.billing.PaymentResponse;
import com.veloia.smartwater.dto.billing.RecordPaymentRequest;
import com.veloia.smartwater.dto.billing.WaterBillResponse;
import com.veloia.smartwater.dto.common.PageResponse;
import com.veloia.smartwater.entity.Customer;
import com.veloia.smartwater.entity.Payment;
import com.veloia.smartwater.entity.WaterBill;
import com.veloia.smartwater.entity.WaterMeter;
import com.veloia.smartwater.exception.BusinessValidationException;
import com.veloia.smartwater.exception.DuplicateResourceException;
import com.veloia.smartwater.exception.ResourceNotFoundException;
import com.veloia.smartwater.mapper.EntityDtoMapper;
import com.veloia.smartwater.repository.CustomerRepository;
import com.veloia.smartwater.repository.PaymentRepository;
import com.veloia.smartwater.repository.WaterBillRepository;
import com.veloia.smartwater.repository.WaterMeterRepository;
import com.veloia.smartwater.service.BillingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BillingServiceImpl implements BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceImpl.class);

    private static final BigDecimal DEFAULT_SIMULATED_BASE_FEE = new BigDecimal("1500.00");
    private static final BigDecimal DEFAULT_SIMULATED_VOLUMETRIC_RATE = new BigDecimal("180.0000");
    private static final BigDecimal DEFAULT_TAX_RATE = new BigDecimal("0.10");

    private final WaterBillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final WaterMeterRepository meterRepository;
    private final EntityDtoMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    public BillingServiceImpl(WaterBillRepository billRepository,
                              PaymentRepository paymentRepository,
                              CustomerRepository customerRepository,
                              WaterMeterRepository meterRepository,
                              EntityDtoMapper mapper) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.meterRepository = meterRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public WaterBillResponse generateBill(GenerateBillRequest request) {
        log.info("Generating water bill for customer ID: {} (Period: {} to {})",
                request.getCustomerId(), request.getBillingPeriodStart(), request.getBillingPeriodEnd());

        if (request.getBillingPeriodEnd().isBefore(request.getBillingPeriodStart())) {
            throw new BusinessValidationException("Billing period end date cannot be before start date");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        if (billRepository.existsByCustomerIdAndBillingPeriodStartAndBillingPeriodEnd(
                customer.getId(), request.getBillingPeriodStart(), request.getBillingPeriodEnd())) {
            throw new DuplicateResourceException("WaterBill", "billingPeriod",
                    "A bill for customer " + customer.getCustomerNumber() + " already exists for period "
                            + request.getBillingPeriodStart() + " to " + request.getBillingPeriodEnd());
        }

        WaterMeter meter = null;
        if (request.getMeterId() != null) {
            meter = meterRepository.findById(request.getMeterId())
                    .orElseThrow(() -> new ResourceNotFoundException("WaterMeter", "id", request.getMeterId()));
        } else if (customer.getMeters() != null && !customer.getMeters().isEmpty()) {
            meter = customer.getMeters().getFirst();
        }

        // Calculate period consumption from readings
        BigDecimal consumptionM3 = BigDecimal.ZERO;
        if (meter != null) {
            String sql = """
                SELECT COALESCE(SUM(incremental_consumption_m3), 0.000)
                FROM meter_readings
                WHERE meter_id = :meterId
                  AND reading_timestamp >= :startTime
                  AND reading_timestamp <= :endTime
                """;
            try {
                Object sumObj = entityManager.createNativeQuery(sql)
                        .setParameter("meterId", meter.getId())
                        .setParameter("startTime", request.getBillingPeriodStart().atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
                        .setParameter("endTime", request.getBillingPeriodEnd().plusDays(1).atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
                        .getSingleResult();
                if (sumObj != null) {
                    consumptionM3 = new BigDecimal(sumObj.toString());
                }
            } catch (Exception e) {
                log.warn("Could not query telemetry readings for bill calculation: {}", e.getMessage());
            }
        }

        // If no readings in period, apply realistic simulated baseline consumption (e.g. 15.5 m3)
        if (consumptionM3.compareTo(BigDecimal.ZERO) == 0) {
            consumptionM3 = new BigDecimal("15.500");
        }

        BigDecimal baseFee = request.getSimulatedBaseFee() != null ? request.getSimulatedBaseFee() : DEFAULT_SIMULATED_BASE_FEE;
        BigDecimal volumetricRate = request.getSimulatedVolumetricRate() != null ? request.getSimulatedVolumetricRate() : DEFAULT_SIMULATED_VOLUMETRIC_RATE;
        BigDecimal consumptionCharge = consumptionM3.multiply(volumetricRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxRate = request.getTaxRatePercent() != null ? request.getTaxRatePercent().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP) : DEFAULT_TAX_RATE;
        BigDecimal taxAmount = baseFee.add(consumptionCharge).multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseFee.add(consumptionCharge).add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : request.getBillingPeriodEnd().plusDays(21);
        String billNumber = "BILL-" + request.getBillingPeriodEnd().format(DateTimeFormatter.ofPattern("yyyyMM"))
                + "-" + customer.getId() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        WaterBill bill = new WaterBill();
        bill.setBillNumber(billNumber);
        bill.setCustomer(customer);
        bill.setMeter(meter);
        bill.setBillingPeriodStart(request.getBillingPeriodStart());
        bill.setBillingPeriodEnd(request.getBillingPeriodEnd());
        bill.setBaseFee(baseFee);
        bill.setVolumetricRate(volumetricRate);
        bill.setConsumptionM3(consumptionM3);
        bill.setConsumptionCharge(consumptionCharge);
        bill.setTaxAmount(taxAmount);
        bill.setTotalAmount(totalAmount);
        bill.setDueDate(dueDate);
        bill.setStatus("ISSUED");

        WaterBill saved = billRepository.save(bill);
        log.info("Bill {} generated successfully with total amount: {} JPY", saved.getBillNumber(), saved.getTotalAmount());
        return mapper.toBillResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse recordPayment(RecordPaymentRequest request) {
        log.info("Recording payment of {} for bill ID: {}", request.getAmount(), request.getBillId());

        WaterBill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("WaterBill", "id", request.getBillId()));

        if ("PAID".equalsIgnoreCase(bill.getStatus())) {
            throw new BusinessValidationException("Bill " + bill.getBillNumber() + " is already marked as PAID.");
        }

        BigDecimal previouslyPaid = (bill.getPayments() != null)
                ? bill.getPayments().stream()
                    .filter(p -> "COMPLETED".equalsIgnoreCase(p.getTransactionStatus()))
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                : BigDecimal.ZERO;

        BigDecimal remainingBalance = bill.getTotalAmount().subtract(previouslyPaid);

        if (request.getAmount().compareTo(remainingBalance) > 0) {
            throw new BusinessValidationException("Payment amount (" + request.getAmount()
                    + ") exceeds the outstanding balance (" + remainingBalance + ") for bill " + bill.getBillNumber());
        }

        String paymentRef = "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Payment payment = new Payment();
        payment.setPaymentReference(paymentRef);
        payment.setBill(bill);
        payment.setPaymentDate(Instant.now());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod().toUpperCase());
        payment.setTransactionStatus("COMPLETED");
        payment.setNotes(request.getNotes() != null ? request.getNotes() : "Simulated payment transaction");

        Payment savedPayment = paymentRepository.save(payment);

        BigDecimal newTotalPaid = previouslyPaid.add(request.getAmount());
        if (newTotalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setStatus("PAID");
        } else {
            bill.setStatus("PARTIALLY_PAID");
        }
        billRepository.save(bill);

        log.info("Payment {} recorded. New bill status: {}", savedPayment.getPaymentReference(), bill.getStatus());
        return mapper.toPaymentResponse(savedPayment);
    }

    @Override
    public WaterBillResponse getBillById(Long id) {
        WaterBill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WaterBill", "id", id));
        return mapper.toBillResponse(bill);
    }

    @Override
    public WaterBillResponse getBillByNumber(String billNumber) {
        WaterBill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("WaterBill", "billNumber", billNumber));
        return mapper.toBillResponse(bill);
    }

    @Override
    public PageResponse<WaterBillResponse> getAllBills(Long customerId, String status, Pageable pageable) {
        Specification<WaterBill> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.trim().toUpperCase()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<WaterBill> page = billRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toBillResponse));
    }

    @Override
    public PageResponse<PaymentResponse> getAllPayments(Long billId, Pageable pageable) {
        Specification<Payment> spec = (root, query, cb) -> {
            if (billId != null) {
                return cb.equal(root.get("bill").get("id"), billId);
            }
            return cb.conjunction();
        };
        Page<Payment> page = paymentRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(mapper::toPaymentResponse));
    }

    @Override
    public List<PaymentResponse> getPaymentsForBill(Long billId) {
        return paymentRepository.findByBillId(billId).stream()
                .map(mapper::toPaymentResponse)
                .toList();
    }
}
