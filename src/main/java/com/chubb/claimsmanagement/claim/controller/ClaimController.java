package com.chubb.claimsmanagement.claim.controller;

import com.chubb.claimsmanagement.claim.dto.CreateClaimRequest;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.service.ClaimService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/claims")
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(@Valid @RequestBody CreateClaimRequest request) {
        ClaimResponse response = claimService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Claim created successfully"));
    }

    @GetMapping("/claims/{claimId}")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaim(@PathVariable UUID claimId) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getClaim(claimId)));
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getClaimsByClaimant(@RequestParam UUID claimantId) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getClaimsByClaimant(claimantId)));
    }
}
