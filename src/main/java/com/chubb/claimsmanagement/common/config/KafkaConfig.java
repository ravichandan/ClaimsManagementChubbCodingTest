package com.chubb.claimsmanagement.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("kafka")
public class KafkaConfig {

    @Bean
    public NewTopic claimEventsTopic() {
        return TopicBuilder.name("claim-events")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "86400000")
                .build();
    }

    @Bean
    public NewTopic claimNotificationsTopic() {
        return TopicBuilder.name("claim-notifications")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "86400000")
                .build();
    }

    @Bean
    public NewTopic claimAssignmentsTopic() {
        return TopicBuilder.name("claim-assignments")
                .partitions(1)
                .replicas(1)
                .config("retention.ms", "86400000")
                .build();
    }
}
