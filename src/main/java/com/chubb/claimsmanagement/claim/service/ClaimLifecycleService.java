package com.chubb.claimsmanagement.claim.service;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.exceptions.BadRequestException;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClaimLifecycleService {

    private final ClaimRepository claimRepository;

    public ClaimLifecycleService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim assignClaim(UUID claimId, UUID staffId) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));

        if (!isAssignable(claim.getStatus())) {
            throw new BadRequestException("Claim cannot be assigned in status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatus.ASSIGNED);
        return claimRepository.save(claim);
    }

    private boolean isAssignable(ClaimStatus status) {
        return status == ClaimStatus.SUBMITTED || status == ClaimStatus.MORE_INFO_REQUESTED;
    }
}
