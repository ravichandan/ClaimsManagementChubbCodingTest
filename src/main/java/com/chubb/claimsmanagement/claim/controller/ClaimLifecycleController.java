package com.chubb.claimsmanagement.claim.controller;

import com.chubb.claimsmanagement.claim.dto.ClaimResponse;
import com.chubb.claimsmanagement.claim.service.ClaimLifecycleService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ClaimLifecycleController {

    private final ClaimLifecycleService claimLifecycleService;

    public ClaimLifecycleController(ClaimLifecycleService claimLifecycleService) {
        this.claimLifecycleService = claimLifecycleService;
    }

    @PostMapping("/claims/{claimNumber}/assign")
    public ResponseEntity<ApiResponse<ClaimResponse>> assignClaim(
            @PathVariable String claimNumber, @RequestParam String staffNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                claimLifecycleService.assignClaim(claimNumber, staffNumber), "Claim assigned successfully"));
    }
}
