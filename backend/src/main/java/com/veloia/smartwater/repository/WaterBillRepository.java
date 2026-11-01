package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.WaterBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaterBillRepository extends JpaRepository<WaterBill, Long>, JpaSpecificationExecutor<WaterBill> {

    @Override
    @EntityGraph(attributePaths = {"customer", "meter", "payments"})
    Optional<WaterBill> findById(Long id);

    @EntityGraph(attributePaths = {"customer", "meter", "payments"})
    Optional<WaterBill> findByBillNumber(String billNumber);

    boolean existsByCustomerIdAndBillingPeriodStartAndBillingPeriodEnd(
            Long customerId, java.time.LocalDate billingPeriodStart, java.time.LocalDate billingPeriodEnd);

    boolean existsByBillNumber(String billNumber);

    @Override
    @EntityGraph(attributePaths = {"customer", "meter"})
    Page<WaterBill> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "meter"})
    Page<WaterBill> findByCustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "meter"})
    Page<WaterBill> findByStatus(String status, Pageable pageable);
}
