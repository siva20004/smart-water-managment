package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {

    Optional<Region> findByCode(String code);

    boolean existsByCode(String code);

    Page<Region> findByStatus(String status, Pageable pageable);

    Page<Region> findByPrefectureIgnoreCase(String prefecture, Pageable pageable);
}
