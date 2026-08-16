package com.chubb.claimsmanagement.claimant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Payload used to register a claimant profile. */
public record CreateClaimantRequest(
        @NotBlank String claimantMemberNumber,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String phone,
        @NotBlank String address,
        @NotBlank String policyNumber
) {
}
