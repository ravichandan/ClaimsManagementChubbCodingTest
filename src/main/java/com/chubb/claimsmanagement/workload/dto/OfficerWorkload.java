package com.chubb.claimsmanagement.workload.dto;

import java.util.List;

/** Workload counts for one claims officer. */
public record OfficerWorkload(
        String staffNumber,
        int assignedClaims,
        int underAssessment,
        List<String> assignedClaimNumbers
) {
}