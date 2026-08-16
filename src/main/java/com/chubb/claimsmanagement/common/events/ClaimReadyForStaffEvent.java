package com.chubb.claimsmanagement.common.events;

import java.io.Serializable;
import java.util.UUID;

/** Event requesting that a submitted claim be made available to staff. */
public record ClaimReadyForStaffEvent(UUID claimId, String claimNumber) implements Serializable {
}