package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.WaterQualityRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WaterQualityRecordRepository extends JpaRepository<WaterQualityRecord, Long>, JpaSpecificationExecutor<WaterQualityRecord> {

    @EntityGraph(attributePaths = {"location", "location.region"})
    Optional<WaterQualityRecord> findBySampleCode(String sampleCode);

    @Override
    @EntityGraph(attributePaths = {"location", "location.region"})
    Page<WaterQualityRecord> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"location", "location.region"})
    Page<WaterQualityRecord> findByLocationId(Long locationId, Pageable pageable);

    @EntityGraph(attributePaths = {"location", "location.region"})
    Page<WaterQualityRecord> findByPotabilityStatus(String potabilityStatus, Pageable pageable);
}
