package com.chubb.claimsmanagement.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJms
@EnableScheduling
/** Enables JMS listeners and scheduled outbox dispatching. */
public class JmsConfig {
}