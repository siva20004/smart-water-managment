package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    @EntityGraph(attributePaths = {"bill", "bill.customer"})
    Optional<Payment> findByPaymentReference(String paymentReference);

    @EntityGraph(attributePaths = {"bill", "bill.customer"})
    List<Payment> findByBillId(Long billId);

    @Override
    @EntityGraph(attributePaths = {"bill", "bill.customer"})
    Page<Payment> findAll(Pageable pageable);
}
