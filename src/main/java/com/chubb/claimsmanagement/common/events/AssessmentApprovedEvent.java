package com.chubb.claimsmanagement.common.events;

import java.io.Serializable;
import java.util.UUID;

/** Event routed to finance after an assessment is approved. */
public record AssessmentApprovedEvent(
        UUID assessmentId,
        UUID claimId,
        String claimNumber,
        String staffNumber,
        Double estimatedAmount,
        Double settledAmount
) implements Serializable {
}