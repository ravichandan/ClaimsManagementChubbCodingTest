package com.chubb.claimsmanagement.claim.service;

import com.chubb.claimsmanagement.claim.dto.CreateClaimRequest;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.claimant.entity.Claimant;
import com.chubb.claimsmanagement.claimant.repository.ClaimantRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimantRepository claimantRepository;

    public ClaimService(ClaimRepository claimRepository, ClaimantRepository claimantRepository) {
        this.claimRepository = claimRepository;
        this.claimantRepository = claimantRepository;
    }

    public ClaimResponse createClaim(CreateClaimRequest request) {
        Claimant claimant = claimantRepository.findById(request.claimantId())
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + request.claimantId()));

        Claim claim = new Claim();
        claim.setClaimant(claimant);
        claim.setClaimType(request.claimType());
        claim.setDescription(request.description());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setClaimNumber(generateClaimNumber());

        Claim saved = claimRepository.save(claim);
        return toResponse(saved);
    }

    public ClaimResponse getClaim(UUID claimId) {
        return claimRepository.findById(claimId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));
    }

    public List<ClaimResponse> getClaimsByClaimant(UUID claimantId) {
        return claimRepository.findByClaimantId(claimantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getClaimNumber(),
                claim.getClaimant().getId(),
                claim.getClaimType(),
                claim.getStatus(),
                claim.getDescription(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }

    private String generateClaimNumber() {
        return "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
