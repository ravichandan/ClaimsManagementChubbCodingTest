package com.chubb.claimsmanagement.claim.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.enums.ClaimType;

import java.time.LocalDateTime;

public record ClaimResponse(
        String claimNumber,
        String claimantMemberNumber,
        ClaimType claimType,
        ClaimStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
