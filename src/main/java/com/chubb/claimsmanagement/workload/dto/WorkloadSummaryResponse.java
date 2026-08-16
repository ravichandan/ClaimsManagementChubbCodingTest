package com.chubb.claimsmanagement.workload.dto;

import java.util.List;

public record WorkloadSummaryResponse(
        List<WorkloadSummary> statusSummary,
        List<StaffAssignmentSummary> staffAssignments
) {
}