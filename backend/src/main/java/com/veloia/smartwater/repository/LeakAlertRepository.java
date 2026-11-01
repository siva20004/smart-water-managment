package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.LeakAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeakAlertRepository extends JpaRepository<LeakAlert, Long>, JpaSpecificationExecutor<LeakAlert> {

    @EntityGraph(attributePaths = {"meter", "location", "location.region"})
    Optional<LeakAlert> findByAlertCode(String alertCode);

    @Override
    @EntityGraph(attributePaths = {"meter", "location", "location.region"})
    Page<LeakAlert> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"meter", "location", "location.region"})
    Page<LeakAlert> findByStatus(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"meter", "location", "location.region"})
    Page<LeakAlert> findBySeverity(String severity, Pageable pageable);

    boolean existsByMeterIdAndStatusNotIn(Long meterId, java.util.Collection<String> closedStatuses);
}
