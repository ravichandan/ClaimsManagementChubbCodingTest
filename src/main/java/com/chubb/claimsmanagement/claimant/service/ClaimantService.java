package com.chubb.claimsmanagement.claimant.service;

import com.chubb.claimsmanagement.claimant.dto.CreateClaimantRequest;
import com.chubb.claimsmanagement.claimant.entity.Claimant;
import com.chubb.claimsmanagement.claimant.repository.ClaimantRepository;
import com.chubb.claimsmanagement.common.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClaimantService {

    private final ClaimantRepository claimantRepository;

    public ClaimantService(ClaimantRepository claimantRepository) {
        this.claimantRepository = claimantRepository;
    }

    public Claimant createClaimant(CreateClaimantRequest request) {
        Claimant claimant = new Claimant();
        claimant.setClaimantMemberNumber(request.claimantMemberNumber());
        claimant.setFirstName(request.firstName());
        claimant.setLastName(request.lastName());
        claimant.setEmail(request.email());
        claimant.setPhone(request.phone());
        claimant.setAddress(request.address());
        claimant.setPolicyNumber(request.policyNumber());
        return claimantRepository.save(claimant);
    }

    public Claimant getClaimant(UUID id) {
        return claimantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + id));
    }

    public Claimant getClaimantByMemberNumber(String claimantMemberNumber) {
        return claimantRepository.findByClaimantMemberNumber(claimantMemberNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Claimant not found: " + claimantMemberNumber));
    }
}
