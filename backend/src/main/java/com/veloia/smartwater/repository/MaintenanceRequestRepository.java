package com.veloia.smartwater.repository;

import com.veloia.smartwater.entity.MaintenanceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long>, JpaSpecificationExecutor<MaintenanceRequest> {

    @EntityGraph(attributePaths = {"meter", "alert", "location", "assignedTechnician"})
    Optional<MaintenanceRequest> findByRequestNumber(String requestNumber);

    @Override
    @EntityGraph(attributePaths = {"meter", "alert", "location", "assignedTechnician"})
    Page<MaintenanceRequest> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"meter", "alert", "location", "assignedTechnician"})
    Page<MaintenanceRequest> findByStatus(String status, Pageable pageable);

    @EntityGraph(attributePaths = {"meter", "alert", "location", "assignedTechnician"})
    Page<MaintenanceRequest> findByPriority(String priority, Pageable pageable);
}
