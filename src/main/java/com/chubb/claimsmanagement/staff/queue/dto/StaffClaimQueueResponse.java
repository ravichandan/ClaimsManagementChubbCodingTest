package com.chubb.claimsmanagement.staff.queue.dto;

import com.chubb.claimsmanagement.common.enums.ClaimStatus;
import com.chubb.claimsmanagement.common.enums.ClaimType;
import com.chubb.claimsmanagement.staff.queue.entity.QueueStatus;

import java.time.LocalDateTime;
/** Public queue item returned to staff-facing clients. */
public record StaffClaimQueueResponse(
        String claimNumber,
        String claimantMemberNumber,
        ClaimType claimType,
        ClaimStatus claimStatus,
        QueueStatus queueStatus,
        String staffNumber,
        LocalDateTime queuedAt,
        LocalDateTime pickedUpAt
) {
}