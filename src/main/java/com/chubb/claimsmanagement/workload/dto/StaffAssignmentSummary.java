package com.chubb.claimsmanagement.workload.dto;

import java.util.List;

public record StaffAssignmentSummary(
        String staffNumber,
        List<AssignedClaimSummary> assignedClaims
) {
}