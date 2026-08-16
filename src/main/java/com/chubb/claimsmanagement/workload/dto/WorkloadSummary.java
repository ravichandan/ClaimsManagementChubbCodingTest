package com.chubb.claimsmanagement.workload.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;

import java.util.List;

public record WorkloadSummary(
        ClaimStatus status,
        long count,
        List<String> claimNumbers
) {
}
