package com.chubb.claimsmanagement.workload.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;

import java.util.List;

/** Claim counts and public claim numbers grouped by lifecycle status. */
public record WorkloadSummary(
        ClaimStatus status,
        long count,
        List<String> claimNumbers
) {
}
