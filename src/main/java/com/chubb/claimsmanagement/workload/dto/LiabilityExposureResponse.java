package com.chubb.claimsmanagement.workload.dto;

import java.math.BigDecimal;

/** Aggregated requested, approved, and outstanding claim liability amounts. */
public record LiabilityExposureResponse(
        BigDecimal totalRequestedAmount,
        BigDecimal totalApprovedAmount,
        BigDecimal outstandingLiabilityExposure
) {
}