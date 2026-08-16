package com.chubb.claimsmanagement.claim.repository;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Persistence operations for claims and public claim-number lookups. */
public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByClaimantId(UUID claimantId);
    List<Claim> findByStatus(ClaimStatus status);
    Optional<Claim> findByClaimNumber(String claimNumber);
}
