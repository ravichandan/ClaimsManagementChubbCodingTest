package com.chubb.claimsmanagement.claimant.repository;

import com.chubb.claimsmanagement.claimant.entity.Claimant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClaimantRepository extends JpaRepository<Claimant, UUID> {
    Optional<Claimant> findByClaimantMemberNumber(String claimantMemberNumber);
}
