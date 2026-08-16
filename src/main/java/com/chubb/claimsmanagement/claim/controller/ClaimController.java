package com.chubb.claimsmanagement.claim.controller;

import com.chubb.claimsmanagement.claim.dto.CreateClaimRequest;
import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.dto.UpdateClaimInformationRequest;
import com.chubb.claimsmanagement.claim.service.ClaimService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
/** REST endpoints for claimant-facing claim operations. */
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/claims")
    /** Creates a claim and places it into the staff intake workflow. */
    public ResponseEntity<ApiResponse<ClaimResponse>> createClaim(@Valid @RequestBody CreateClaimRequest request) {
        ClaimResponse response = claimService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Claim created successfully"));
    }

    @GetMapping("/claims/{claimNumber}")
    /** Retrieves a claim using its public claim number. */
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaim(@PathVariable String claimNumber) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getClaimByNumber(claimNumber)));
    }

    @PutMapping("/claims/{claimNumber}/more-information")
    /** Accepts claimant information requested during staff review. */
    public ResponseEntity<ApiResponse<ClaimResponse>> updateClaimInformation(
            @PathVariable String claimNumber,
            @Valid @RequestBody UpdateClaimInformationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                claimService.updateClaimInformation(claimNumber, request),
                "Claim information updated successfully"));
    }

    @GetMapping("/claims")
    /** Lists claims belonging to a claimant member number. */
    public ResponseEntity<ApiResponse<List<ClaimResponse>>> getClaimsByClaimant(
            @RequestParam String claimantMemberNumber) {
        return ResponseEntity.ok(ApiResponse.success(claimService.getClaimsByClaimantMemberNumber(claimantMemberNumber)));
    }
}
