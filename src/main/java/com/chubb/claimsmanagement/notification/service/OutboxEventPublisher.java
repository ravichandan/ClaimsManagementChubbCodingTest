package com.chubb.claimsmanagement.notification.service;

import com.chubb.claimsmanagement.common.events.AssessmentApprovedEvent;
import com.chubb.claimsmanagement.common.events.AssessmentRejectedEvent;
import com.chubb.claimsmanagement.common.events.ClaimReadyForStaffEvent;
import com.chubb.claimsmanagement.common.events.ClaimSubmittedEvent;
import com.chubb.claimsmanagement.notification.outbox.OutboxEvent;
import com.chubb.claimsmanagement.notification.outbox.OutboxEventRepository;
import com.chubb.claimsmanagement.notification.outbox.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final Map<String, Class<?>> eventTypes = Map.of(
            ClaimSubmittedEvent.class.getName(), ClaimSubmittedEvent.class,
            ClaimReadyForStaffEvent.class.getName(), ClaimReadyForStaffEvent.class,
            AssessmentApprovedEvent.class.getName(), AssessmentApprovedEvent.class,
            AssessmentRejectedEvent.class.getName(), AssessmentRejectedEvent.class
    );

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository,
                                ObjectMapper objectMapper,
                                JmsTemplate jmsTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.jmsTemplate = jmsTemplate;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:1000}")
    @Transactional
    public void publishPendingEvents() {
        for (OutboxEvent event : outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            try {
                Class<?> eventClass = eventTypes.get(event.getEventType());
                if (eventClass == null) {
                    throw new IllegalStateException("Unsupported outbox event type: " + event.getEventType());
                }
                Object payload = objectMapper.readValue(event.getPayload(), eventClass);
                jmsTemplate.convertAndSend(event.getDestination(), payload);
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(null);
            } catch (JsonProcessingException | RuntimeException exception) {
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(exception.getMessage());
                log.error("Could not publish outbox event {}", event.getId(), exception);
            }
        }
    }
}