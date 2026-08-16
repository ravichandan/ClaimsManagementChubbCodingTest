package com.chubb.claimsmanagement.assessment.dto;

import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssessmentRequest(
        @NotBlank String claimNumber,
        @NotBlank String staffNumber,
        @NotBlank String assessmentType,
        @NotBlank String description,
        String details,
        @NotNull Double estimatedAmount,
        @NotNull AssessmentResult result
) {
}
