package com.smartlogi.mail.service;

// Importing required classes
import java.io.File;

import com.smartlogi.mail.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    public String sendMail(EmailDetails details)
    {
        try {
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";
        }

        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    public String sendMailWithHTML(EmailDetails details) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject(details.getSubject());
            helper.setText(details.getMsgBody(), true);

            javaMailSender.send(message);
            return "HTML Mail Sent Successfully...";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error while Sending HTML Mail";
        }
    }
}