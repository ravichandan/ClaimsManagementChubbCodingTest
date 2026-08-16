package com.chubb.claimsmanagement.claim.dto;

import jakarta.validation.constraints.NotBlank;

/** Payload containing claimant information requested by staff. */
public record UpdateClaimInformationRequest(
        @NotBlank String description
) {
}