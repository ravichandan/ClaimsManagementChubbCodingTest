package com.chubb.claimsmanagement.claim.service;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.exceptions.BadRequestException;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.staff.repository.StaffRepository;
import org.springframework.stereotype.Service;

@Service
public class ClaimLifecycleService {

    private final ClaimRepository claimRepository;
    private final StaffRepository staffRepository;

    public ClaimLifecycleService(ClaimRepository claimRepository, StaffRepository staffRepository) {
        this.claimRepository = claimRepository;
        this.staffRepository = staffRepository;
    }

        public ClaimResponse assignClaim(String claimNumber, String staffNumber) {
        staffRepository.findByStaffNumber(staffNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffNumber));
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));

        if (!isAssignable(claim.getStatus())) {
            throw new BadRequestException("Claim cannot be assigned in status: " + claim.getStatus());
        }

        claim.setStatus(ClaimStatus.ASSIGNED);
        Claim saved = claimRepository.save(claim);
        return new ClaimResponse(
            saved.getClaimNumber(),
            saved.getClaimant().getClaimantMemberNumber(),
            saved.getClaimType(),
            saved.getStatus(),
            saved.getDescription(),
            saved.getCreatedAt(),
            saved.getUpdatedAt()
        );
    }

    private boolean isAssignable(ClaimStatus status) {
        return status == ClaimStatus.SUBMITTED || status == ClaimStatus.MORE_INFO_REQUESTED;
    }
}
