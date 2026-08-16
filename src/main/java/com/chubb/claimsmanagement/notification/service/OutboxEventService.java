package com.chubb.claimsmanagement.notification.service;

import com.chubb.claimsmanagement.notification.outbox.OutboxEvent;
import com.chubb.claimsmanagement.notification.outbox.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enqueue(String destination, Object event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(event.getClass().getName());
        outboxEvent.setDestination(destination);
        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize outbox event", exception);
        }
        outboxEventRepository.save(outboxEvent);
    }
}