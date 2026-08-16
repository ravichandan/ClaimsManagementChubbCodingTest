package com.chubb.claimsmanagement.notification.service;

import com.chubb.claimsmanagement.common.events.ClaimSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishClaimSubmitted(ClaimSubmittedEvent event) {
        kafkaTemplate.send("claim-events", event.claimNumber(), event);
        log.info("Published claim submitted event for claim {}", event.claimNumber());
    }
}
