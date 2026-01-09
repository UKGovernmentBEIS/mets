package uk.gov.pmrv.api.notification.mail.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.Email;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailRecipients;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.notificationapi.mail.service.SendEmailService;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

/**
 * Service implementation for generating and sending email notifications
 */
@Log4j2
@Service
@ConditionalOnProperty(name = "env.isProd", havingValue = "true")
public abstract class NotificationEmailBaseServiceImpl<T extends EmailNotificationTemplateData> implements NotificationEmailService<T> {

    protected final SendEmailService sendEmailService;
    protected final NotificationTemplateProcessService notificationTemplateProcessService;
    protected final NotificationProperties notificationProperties;

    public NotificationEmailBaseServiceImpl(SendEmailService sendEmailService,
                                        NotificationTemplateProcessService notificationTemplateProcessService,
                                        NotificationProperties notificationProperties) {
        this.sendEmailService = sendEmailService;
        this.notificationTemplateProcessService = notificationTemplateProcessService;
        this.notificationProperties = notificationProperties;
    }

    @Override
    public void notifyRecipient(EmailData<T> emailData, String recipientEmail) {
        this.notifyRecipients(emailData, List.of(recipientEmail), Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public void notifyRecipients(EmailData<T> emailData, List<String> recipientsEmails) {
        this.notifyRecipients(emailData, recipientsEmails, Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public void notifyRecipients(EmailData<T> emailData, List<String> recipientsEmails, List<String> ccRecipientsEmails) {
        this.notifyRecipients(emailData, recipientsEmails, ccRecipientsEmails, Collections.emptyList());
    }

    @Override
    public void notifyRecipients(EmailData<T> emailData, List<String> recipientsEmails, List<String> ccRecipientsEmails, List<String> bccRecipientsEmails) {
        final T notificationTemplateData = emailData.getNotificationTemplateData();
        final NotificationContent emailNotificationContent = buildNotificationContent(notificationTemplateData);

        final Email email = Email.builder()
                .from(notificationProperties.getEmail().getAutoSender())
                .recipients(EmailRecipients.builder()
                        .to(recipientsEmails)
                        .cc(ccRecipientsEmails)
                        .bcc(bccRecipientsEmails)
                        .build())
                .subject(emailNotificationContent.getSubject())
                .text(createEmailText(emailNotificationContent))
                .attachments(emailData.getAttachments())
                .build();

        //send the email
        CompletableFuture.runAsync(() -> sendEmailService.sendMail(email));
    }
    
    protected abstract NotificationContent buildNotificationContent(T notificationTemplateData);
    
    protected String createEmailText(NotificationContent notificationContent) {
        return notificationContent.getText();
    }

}
