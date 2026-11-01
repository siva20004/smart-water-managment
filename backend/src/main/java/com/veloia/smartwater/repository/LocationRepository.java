package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    @EntityGraph(attributePaths = {"region"})
    Optional<Location> findByLocationCode(String locationCode);

    boolean existsByLocationCode(String locationCode);

    @Override
    @EntityGraph(attributePaths = {"region"})
    Page<Location> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"region"})
    Optional<Location> findById(Long id);

    @EntityGraph(attributePaths = {"region"})
    Page<Location> findByRegionId(Long regionId, Pageable pageable);

    @EntityGraph(attributePaths = {"region"})
    Page<Location> findByCityIgnoreCase(String city, Pageable pageable);

    @EntityGraph(attributePaths = {"region"})
    Page<Location> findByInstallationType(String installationType, Pageable pageable);
}
