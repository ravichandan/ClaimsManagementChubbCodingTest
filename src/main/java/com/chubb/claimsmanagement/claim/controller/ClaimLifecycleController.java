package com.chubb.claimsmanagement.claim.controller;

import com.chubb.claimsmanagement.claim.entity.Claim;
import com.chubb.claimsmanagement.claim.service.ClaimLifecycleService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ClaimLifecycleController {

    private final ClaimLifecycleService claimLifecycleService;

    public ClaimLifecycleController(ClaimLifecycleService claimLifecycleService) {
        this.claimLifecycleService = claimLifecycleService;
    }

    @PostMapping("/claims/{claimId}/assign")
    public ResponseEntity<ApiResponse<Claim>> assignClaim(@PathVariable UUID claimId, @RequestParam UUID staffId) {
        return ResponseEntity.ok(ApiResponse.success(claimLifecycleService.assignClaim(claimId, staffId), "Claim assigned successfully"));
    }
}
