package com.chubb.claimsmanagement.common.events;

import java.io.Serializable;
import java.util.UUID;

public record AssessmentApprovedEvent(
        UUID assessmentId,
        UUID claimId,
        String claimNumber,
        String staffNumber,
        Double estimatedAmount,
        Double settledAmount
) implements Serializable {
}