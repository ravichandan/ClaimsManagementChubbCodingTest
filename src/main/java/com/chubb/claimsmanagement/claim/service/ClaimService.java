package com.chubb.claimsmanagement.claim.service;

import com.chubb.claimsmanagement.claim.dto.CreateClaimRequest;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.repository.ClaimRepository;
import com.chubb.claimsmanagement.claimant.entity.Claimant;
import com.chubb.claimsmanagement.claimant.repository.ClaimantRepository;
import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.events.ClaimReadyForStaffEvent;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import com.chubb.claimsmanagement.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimantRepository claimantRepository;
    private final NotificationService notificationService;

    public ClaimService(ClaimRepository claimRepository, ClaimantRepository claimantRepository,
                        NotificationService notificationService) {
        this.claimRepository = claimRepository;
        this.claimantRepository = claimantRepository;
        this.notificationService = notificationService;
    }

    public ClaimResponse createClaim(CreateClaimRequest request) {
        Claimant claimant = claimantRepository.findByClaimantMemberNumber(request.claimantMemberNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + request.claimantMemberNumber()));

        Claim claim = new Claim();
        claim.setClaimant(claimant);
        claim.setClaimType(request.claimType());
        claim.setDescription(request.description());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setClaimNumber(generateClaimNumber());

        Claim saved = claimRepository.save(claim);
        notificationService.publishClaimReadyForStaff(new ClaimReadyForStaffEvent(
            saved.getId(), saved.getClaimNumber()));
        return toResponse(saved);
    }

    public ClaimResponse getClaim(UUID claimId) {
        return claimRepository.findById(claimId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimId));
    }

    public ClaimResponse getClaimByNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found: " + claimNumber));
    }

    public List<ClaimResponse> getClaimsByClaimantMemberNumber(String claimantMemberNumber) {
        Claimant claimant = claimantRepository.findByClaimantMemberNumber(claimantMemberNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + claimantMemberNumber));

        return claimRepository.findByClaimantId(claimant.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNumber(),
                claim.getClaimant().getClaimantMemberNumber(),
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
