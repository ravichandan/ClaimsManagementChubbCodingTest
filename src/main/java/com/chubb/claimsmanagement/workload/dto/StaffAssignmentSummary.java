package com.chubb.claimsmanagement.workload.dto;

import java.util.List;

/** Staff member and claims currently assigned to that member. */
public record StaffAssignmentSummary(
        String staffNumber,
        List<AssignedClaimSummary> assignedClaims
) {
}