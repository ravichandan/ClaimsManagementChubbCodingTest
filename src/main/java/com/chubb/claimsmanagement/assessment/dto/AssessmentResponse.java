package com.chubb.claimsmanagement.assessment.dto;

import com.chubb.claimsmanagement.common.enums.AssessmentResult;

import java.time.LocalDateTime;
import java.util.UUID;

/** Public assessment representation returned by the API. */
public record AssessmentResponse(
        UUID id,
        String claimNumber,
        String staffNumber,
        String assessmentType,
        String description,
        String details,
        Double estimatedAmount,
        Double settledAmount,
        AssessmentResult result,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
