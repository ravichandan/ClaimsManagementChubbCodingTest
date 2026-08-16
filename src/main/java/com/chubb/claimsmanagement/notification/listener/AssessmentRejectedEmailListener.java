package com.chubb.claimsmanagement.notification.listener;

import com.chubb.claimsmanagement.common.events.AssessmentRejectedEvent;
// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
/**
 * Listens for rejected assessments and represents the customer email notification step.
 *
 * For the current time constraints, the actual SMTP integration is mocked by logging
 * the notification details instead of sending an email.
 */
public class AssessmentRejectedEmailListener {

    private static final Logger log = LoggerFactory.getLogger(AssessmentRejectedEmailListener.class);

    // JavaMailSender will replace the logging mock when SMTP integration is added.

    public AssessmentRejectedEmailListener() {
    }

    @JmsListener(destination = "assessment-rejected-queue")
    public void handle(AssessmentRejectedEvent event) {
        // Mock email delivery until the mail provider and credentials are configured.
        log.info("Sent {} email for claim {} to {}", event.result(), event.claimNumber(), event.claimantEmail());
    }
}