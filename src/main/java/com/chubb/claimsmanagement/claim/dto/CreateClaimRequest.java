package com.chubb.claimsmanagement.claim.dto;

import com.chubb.claimsmanagement.common.enums.ClaimType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateClaimRequest(
        @NotBlank String claimantMemberNumber,
        @NotNull ClaimType claimType,
        @NotBlank String description
) {
}
