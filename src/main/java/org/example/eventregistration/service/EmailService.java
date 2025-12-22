package org.example.eventregistration.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Notifies a user via email that they have been added to a group.
     * Runs asynchronously to prevent UI blocking.
     */
    @Async
    public void sendGroupNotification(String toEmail, String groupName, String adminName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@gather.app");
            message.setTo(toEmail);
            message.setSubject("You have been added to " + groupName + "!");

            String text = String.format(
                    "Hi there,\n\n%s has added you to the group '%s' on Gather.\n\n" +
                            "Log in now to see upcoming events and chat with the squad!\n\n" +
                            "Cheers,\nThe Gather Team",
                    adminName, groupName
            );

            message.setText(text);
            emailSender.send(message);

            logger.info("Group addition notification sent to {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send notification email to {}", toEmail, e);
        }
    }
}