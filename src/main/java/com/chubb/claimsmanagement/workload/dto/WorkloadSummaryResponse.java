package com.chubb.claimsmanagement.workload.dto;

import java.math.BigDecimal;
import java.util.List;

/** Combined management dashboard response for claims workload and liability. */
public record WorkloadSummaryResponse(
        int totalClaims,
        BigDecimal liabilityExposure,
        int assignedClaims,
        int underAssessment,
        int unassignedClaims,
        int outstandingClaims,
        List<OfficerWorkload> officerWorkloads
) {
}