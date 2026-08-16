package com.chubb.claimsmanagement.notification.service;

import com.chubb.claimsmanagement.common.events.ClaimSubmittedEvent;
import com.chubb.claimsmanagement.common.events.ClaimReadyForStaffEvent;
import com.chubb.claimsmanagement.common.events.AssessmentApprovedEvent;
import com.chubb.claimsmanagement.common.events.AssessmentRejectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
/** Stores domain events in the transactional outbox for asynchronous delivery. */
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CLAIM_EVENTS_QUEUE = "claim-events";
    private static final String STAFF_CLAIM_QUEUE = "staff-claim-queue";
    private static final String FINANCE_TEAM_QUEUE = "finance-team-queue";
    private static final String ASSESSMENT_REJECTED_QUEUE = "assessment-rejected-queue";

    private final OutboxEventService outboxEventService;

    public NotificationService(OutboxEventService outboxEventService) {
        this.outboxEventService = outboxEventService;
    }

    /** Stores a legacy claim-submitted event for downstream consumers. */
    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        outboxEventService.enqueue(CLAIM_EVENTS_QUEUE, event);
        log.info("Stored claim submitted event for claim {} in the outbox", event.claimNumber());
    }

    /** Stores a claim intake event for staff queue processing. */
    public void publishClaimReadyForStaff(ClaimReadyForStaffEvent event) {
        outboxEventService.enqueue(STAFF_CLAIM_QUEUE, event);
        log.info("Stored claim ready for staff event for claim {} in the outbox", event.claimNumber());
    }

    /** Stores an approved assessment event for finance processing. */
    public void publishAssessmentApproved(AssessmentApprovedEvent event) {
        outboxEventService.enqueue(FINANCE_TEAM_QUEUE, event);
        log.info("Stored approved assessment for claim {} in the outbox", event.claimNumber());
    }

    /** Stores a rejected assessment event for claimant notification. */
    public void publishAssessmentRejected(AssessmentRejectedEvent event) {
        outboxEventService.enqueue(ASSESSMENT_REJECTED_QUEUE, event);
        log.info("Stored rejected assessment for claim {} in the outbox", event.claimNumber());
    }
}
