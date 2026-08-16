package com.chubb.claimsmanagement.claim.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.enums.ClaimType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClaimResponse(
        UUID id,
        String claimNumber,
        UUID claimantId,
        ClaimType claimType,
        ClaimStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
