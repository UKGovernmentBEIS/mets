package uk.gov.pmrv.api.notification.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.Email;
import uk.gov.netz.api.notificationapi.mail.service.SendEmailService;

import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Log4j2
@Service
@RequiredArgsConstructor
public class PmrvJavaSendEmailServiceImpl implements SendEmailService {

    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    @Override
    public void sendMail(Email email) {
        log.debug("Sending mail with subject {} to: {}", email::getSubject,
            () -> String.join(",", email.getRecipients().toString()));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            
            if(notificationProperties.getSmtpHeaders() != null &&
            		notificationProperties.getSmtpHeaders().getEmailOriginator() != null) {
            	message.addHeader("X-SES-CONFIGURATION-SET", notificationProperties.getSmtpHeaders().getEmailOriginator());
            }
            
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(email.getFrom());
            
            if(email.getRecipients().getTo() != null) {
            	messageHelper.setTo(email.getRecipients().getTo().toArray(String[]::new));	
            }
            
            if(email.getRecipients().getCc() != null) {
            	messageHelper.setCc(email.getRecipients().getCc().toArray(String[]::new));
            }
            
            if(email.getRecipients().getBcc() != null) {
            	messageHelper.setBcc(email.getRecipients().getBcc().toArray(String[]::new));
            }
            
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(email.getText(), true);
            
            for(Map.Entry<String, byte[]> attachment : email.getAttachments().entrySet()) {
                messageHelper.addAttachment(attachment.getKey(), new ByteArrayResource(attachment.getValue()));    
            }

            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error("Exception during sending email:", e);
        }
    }
}
