package com.chubb.claimsmanagement.common.events;

import java.util.UUID;

public record ClaimSubmittedEvent(UUID claimId, UUID claimantId, String claimNumber) {
}
