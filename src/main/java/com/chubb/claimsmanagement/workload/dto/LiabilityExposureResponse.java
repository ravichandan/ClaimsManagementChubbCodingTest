package com.chubb.claimsmanagement.workload.dto;

import java.math.BigDecimal;

public record LiabilityExposureResponse(
        BigDecimal totalRequestedAmount,
        BigDecimal totalApprovedAmount,
        BigDecimal outstandingLiabilityExposure
) {
}