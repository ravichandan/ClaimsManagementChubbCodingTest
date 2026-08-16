package com.chubb.claimsmanagement.assessment.dto;

import com.chubb.claimsmanagement.common.enums.AssessmentResult;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAssessmentRequest(
        @NotNull UUID claimId,
        @NotNull UUID staffId,
        @NotBlank String assessmentType,
        @NotBlank String description,
        String details,
        @NotNull Double estimatedAmount,
        @NotNull AssessmentResult result
) {
}
