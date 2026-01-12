package org.example.eventregistration.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Async
    public void sendVerificationEmail(String to, String code) {
        Email from = new Email("a.ghasr77@gmail.com");
        String subject = "Gather App - Verify your email";
        Email toEmail = new Email(to);
        Content content = new Content("text/plain",
                "Welcome to Gather! \n\nYour verification code is: " + code);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            logger.info("Verification email sent to {}. Status: {}", to, response.getStatusCode());
        } catch (IOException ex) {
            logger.error("Failed to send verification email to {}: {}", to, ex.getMessage(), ex);
        }
    }

    @Async
    public void sendGroupNotification(String toEmail, String groupName, String adminName) {
        Email from = new Email("a.ghasr77@gmail.com");
        String subject = "You have been added to " + groupName + "!";
        Email to = new Email(toEmail);

        String text = String.format(
                "Hi there,\n\n%s has added you to the group '%s' on Gather.\n\n" +
                        "Log in now to see upcoming events and chat with the squad!\n\n" +
                        "Cheers,\nThe Gather Team",
                adminName, groupName
        );

        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            logger.info("Group notification sent to {}. Status: {}", toEmail, response.getStatusCode());
        } catch (IOException ex) {
            logger.error("Failed to send group notification to {}: {}", toEmail, ex.getMessage(), ex);
        }
    }
}