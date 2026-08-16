package com.chubb.claimsmanagement.workload.dto;

import java.util.List;

/** Combined workload response containing status and staffing views. */
public record WorkloadSummaryResponse(
        List<WorkloadSummary> statusSummary,
        List<StaffAssignmentSummary> staffAssignments
) {
}