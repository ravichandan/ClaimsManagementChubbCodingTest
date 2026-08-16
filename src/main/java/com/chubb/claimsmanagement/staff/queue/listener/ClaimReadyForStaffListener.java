package com.chubb.claimsmanagement.staff.queue.listener;

import com.chubb.claimsmanagement.common.events.ClaimReadyForStaffEvent;
import com.chubb.claimsmanagement.staff.queue.service.StaffClaimQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
/** Consumes claim intake events and creates durable staff queue entries. */
public class ClaimReadyForStaffListener {

    private static final Logger log = LoggerFactory.getLogger(ClaimReadyForStaffListener.class);

    private final StaffClaimQueueService queueService;

    public ClaimReadyForStaffListener(StaffClaimQueueService queueService) {
        this.queueService = queueService;
    }

    @JmsListener(destination = "staff-claim-queue")
    /** Idempotently enqueues a newly submitted claim for staff pickup. */
    public void handle(ClaimReadyForStaffEvent event) {
        queueService.enqueue(event.claimId());
        log.info("Enqueued claim {} for staff pickup", event.claimNumber());
    }
}