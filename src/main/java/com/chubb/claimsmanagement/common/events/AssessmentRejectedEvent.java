package com.chubb.claimsmanagement.common.events;

import java.io.Serializable;
import java.util.UUID;

import com.chubb.claimsmanagement.common.enums.AssessmentResult;

public record AssessmentRejectedEvent(
        UUID assessmentId,
        UUID claimId,
        String claimNumber,
        String claimantFirstName,
        String claimantEmail,
        AssessmentResult result,
        String assessmentDescription
) implements Serializable {
}