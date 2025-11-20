package com.smartlogi.delivery.mail.service;

import com.smartlogi.delivery.enums.Status;
import com.smartlogi.delivery.model.Colis;
import com.smartlogi.delivery.model.Livreur;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public String sendColisCreatedEmail(Colis colis) {
        try {
            Context ctx = new Context();
            ctx.setVariable("senderName", colis.getSender().getNom() + " " + colis.getSender().getPrenom());
            ctx.setVariable("creationDate", LocalDate.now());
            ctx.setVariable("colisId", colis.getId());
            ctx.setVariable("status", colis.getStatus().toString());
            ctx.setVariable("destination", colis.getVileDistination());
            ctx.setVariable("receiverName", colis.getReceiver().getNom() + " " + colis.getReceiver().getPrenom());
            ctx.setVariable("weight", colis.getPoids());

            String htmlBody = templateEngine.process("email/colis-created", ctx);

            return sendHtmlMail(colis.getSender().getEmail(),
                    "📦 Your Colis Has Been Successfully Created — [Tracking ID: " + colis.getId() + "]",
                    htmlBody);
        } catch (Exception e) {
            return "Error while sending creation email.";
        }
    }

    public String sendColisAssignedEmail(Colis colis, Livreur livreur) {
        try {
            Context ctx = new Context();
            ctx.setVariable("senderName", colis.getSender().getNom() + " " + colis.getSender().getPrenom());
            ctx.setVariable("colisId", colis.getId());
            ctx.setVariable("destination", colis.getVileDistination());
            ctx.setVariable("receiverName", colis.getReceiver().getNom() + " " + colis.getReceiver().getPrenom());
            ctx.setVariable("status", colis.getStatus().toString());
            ctx.setVariable("livreurName", livreur.getNom() + " " + livreur.getPrenom());
            ctx.setVariable("livreurCity", livreur.getCity().getNom());
            ctx.setVariable("livreurPhone", livreur.getTelephone());

            String htmlBody = templateEngine.process("email/colis-assigned", ctx);

            return sendHtmlMail(colis.getSender().getEmail(),
                    "🚚 Your Colis Has Been Assigned to a Livreur — [Tracking ID: " + colis.getId() + "]",
                    htmlBody);
        } catch (Exception e) {
            return "Error while sending assignment email.";
        }
    }

    public String sendColisStatusUpdatedEmail(Colis colis, Livreur livreur, Status oldStatus, Status newStatus) {
        try {
            Context ctx = new Context();
            ctx.setVariable("senderName", colis.getSender().getNom() + " " + colis.getSender().getPrenom());
            ctx.setVariable("colisId", colis.getId());
            ctx.setVariable("livreurName", livreur.getNom() + " " + livreur.getPrenom());
            ctx.setVariable("oldStatus", oldStatus);
            ctx.setVariable("newStatus", newStatus);

            String htmlBody = templateEngine.process("email/colis-status", ctx);

            return sendHtmlMail(colis.getSender().getEmail(),
                    "🔄 Colis Status Updated — [Tracking ID: " + colis.getId() + "]",
                    htmlBody);
        } catch (Exception e) {
            return "Error while sending status update email.";
        }
    }

    private String sendHtmlMail(String recipient, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("smartlogi.noreply@gmail.com");
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
        return "Email sent successfully.";
    }
}
