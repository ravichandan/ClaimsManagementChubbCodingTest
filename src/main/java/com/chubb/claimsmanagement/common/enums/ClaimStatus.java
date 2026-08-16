package com.chubb.claimsmanagement.common.enums;

/** Lifecycle states used by the claim workflow. */
public enum ClaimStatus {
    SUBMITTED,
    ASSIGNED,
    ASSESSMENT_IN_PROGRESS,
    MORE_INFO_REQUESTED,
    MORE_INFO_PROVIDED,
    APPROVED,
    REJECTED,
    SETTLEMENT_IN_PROGRESS,
    CLOSED
}
