package com.chubb.claimsmanagement.workload.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;

public record AssignedClaimSummary(
        String claimNumber,
        ClaimStatus status
) {
}