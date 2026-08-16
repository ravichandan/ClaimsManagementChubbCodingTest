package com.chubb.claimsmanagement.notification.service;

import com.chubb.claimsmanagement.common.events.ClaimSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String CLAIM_EVENTS_QUEUE = "claim-events";

    private final JmsTemplate jmsTemplate;

    public NotificationService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        jmsTemplate.convertAndSend(CLAIM_EVENTS_QUEUE, event);
        log.info("Published claim submitted event for claim {}", event.claimNumber());
    }
}
