package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.MeterReading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long>, JpaSpecificationExecutor<MeterReading> {

    @EntityGraph(attributePaths = {"meter"})
    Page<MeterReading> findByMeterIdOrderByReadingTimestampDesc(Long meterId, Pageable pageable);

    @EntityGraph(attributePaths = {"meter"})
    Optional<MeterReading> findTopByMeterIdOrderByReadingTimestampDesc(Long meterId);

    boolean existsByMeterIdAndReadingTimestamp(Long meterId, Instant readingTimestamp);
}
