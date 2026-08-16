package com.chubb.claimsmanagement.claimant.controller;

import com.chubb.claimsmanagement.claimant.dto.CreateClaimantRequest;
import com.chubb.claimsmanagement.claimant.entity.Claimant;
import com.chubb.claimsmanagement.claimant.service.ClaimantService;
import com.chubb.claimsmanagement.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
/** REST endpoints for claimant creation and lookup. */
public class ClaimantController {

    private final ClaimantService claimantService;

    public ClaimantController(ClaimantService claimantService) {
        this.claimantService = claimantService;
    }

    @PostMapping("/claimants")
    /** Registers a claimant profile. */
    public ResponseEntity<ApiResponse<Claimant>> createClaimant(@Valid @RequestBody CreateClaimantRequest request) {
        Claimant claimant = claimantService.createClaimant(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(claimant, "Claimant created successfully"));
    }

    @GetMapping("/claimants/{id}")
    /** Retrieves a claimant by internal identifier. */
    public ResponseEntity<ApiResponse<Claimant>> getClaimant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(claimantService.getClaimant(id)));
    }
}
