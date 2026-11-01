package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.WaterMeter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaterMeterRepository extends JpaRepository<WaterMeter, Long>, JpaSpecificationExecutor<WaterMeter> {

    @EntityGraph(attributePaths = {"customer", "location", "location.region"})
    Optional<WaterMeter> findBySerialNumber(String serialNumber);

    boolean existsBySerialNumber(String serialNumber);

    @Override
    @EntityGraph(attributePaths = {"customer", "location", "location.region"})
    Page<WaterMeter> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"customer", "location", "location.region"})
    Optional<WaterMeter> findById(Long id);

    @EntityGraph(attributePaths = {"customer", "location", "location.region"})
    List<WaterMeter> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = {"customer", "location", "location.region"})
    Page<WaterMeter> findByStatus(String status, Pageable pageable);
}
