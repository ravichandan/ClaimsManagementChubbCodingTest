package com.chubb.claimsmanagement.assessment.repository;

import com.chubb.claimsmanagement.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    List<Assessment> findByClaimId(UUID claimId);
    List<Assessment> findByStaffId(UUID staffId);
    Optional<Assessment> findFirstByClaimIdOrderByCreatedAtDesc(UUID claimId);
}
