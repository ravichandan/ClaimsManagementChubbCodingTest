package com.chubb.claimsmanagement.workload.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;

/** Public claim identifier and current status for an assigned claim. */
public record AssignedClaimSummary(
        String claimNumber,
        ClaimStatus status
) {
}