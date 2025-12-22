package org.example.eventregistration.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendGroupInvite(String toEmail, String groupName, String adminName) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@gather.app");
        message.setTo(toEmail);
        message.setSubject("You're invited to join " + groupName + "!");

        String text = String.format(
                "Hi there,\n\n%s has invited you to join the group '%s' on Gather.\n\n" +
                        "Log in now to see upcoming events and chat with the squad!\n\n" +
                        "Cheers,\nThe Gather Team",
                adminName, groupName
        );

        message.setText(text);

        // Send asynchronously logic could go here, but keep it simple for now
        emailSender.send(message);
        System.out.println("Email sent to " + toEmail);
    }
}