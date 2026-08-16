package com.chubb.claimsmanagement.claim.dto;

import com.chubb.claimsmanagement.common.enums.ClaimType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateClaimRequest(
        @NotNull UUID claimantId,
        @NotNull ClaimType claimType,
        @NotBlank String description
) {
}
