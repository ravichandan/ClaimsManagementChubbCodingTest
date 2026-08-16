package com.chubb.claimsmanagement.common.events;

import java.io.Serializable;
import java.util.UUID;

public record ClaimReadyForStaffEvent(UUID claimId, String claimNumber) implements Serializable {
}