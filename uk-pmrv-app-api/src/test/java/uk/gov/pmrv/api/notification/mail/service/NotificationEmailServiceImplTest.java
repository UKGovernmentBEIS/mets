package uk.gov.pmrv.api.notification.mail.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.Email;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailRecipients;
import uk.gov.netz.api.notificationapi.mail.service.SendEmailService;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEmailServiceImplTest {

    private static final String sendFrom = "autoSender@mail";
    
    @InjectMocks
    private NotificationEmailServiceImpl service;

    @Mock
    private SendEmailService sendEmailService;

    @Mock
    private NotificationTemplateProcessService notificationTemplateProcessService;
    
    @Mock
    private NotificationProperties notificationProperties;

    @Test
    void notifyRecipient() throws InterruptedException, IOException {
        EmailData emailData = buildEmailData();
        String recipientEmail = "recipient@email";
        NotificationContent emailNotificationContent = NotificationContent.builder()
                .subject("subject")
                .text("text")
                .build();
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(notificationTemplateProcessService.processEmailNotificationTemplate(
                emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams()))
                .thenReturn(emailNotificationContent);

        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getAutoSender()).thenReturn(sendFrom);

        service.notifyRecipient(emailData, recipientEmail);

        Thread.sleep(100);

        Email expectedEmail = Email.builder()
                .from(sendFrom)
                .recipients(EmailRecipients.builder()
                        .to(List.of(recipientEmail))
                        .cc(Collections.emptyList())
                        .bcc(Collections.emptyList())
                        .build())
                .subject(emailNotificationContent.getSubject())
                .text(emailNotificationContent.getText())
                .attachments(emailData.getAttachments())
                .build();
        verify(sendEmailService, times(1)).sendMail(expectedEmail);
        verify(notificationTemplateProcessService, times(1)).processEmailNotificationTemplate(emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams());
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmail, times(1)).getAutoSender();
    }

    @Test
    void notifyRecipients() throws InterruptedException, IOException {
        EmailData emailData = buildEmailData();
        String recipientEmail = "recipient@email";
        NotificationContent emailNotificationContent = NotificationContent.builder()
                .subject("subject")
                .text("text")
                .build();
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(notificationTemplateProcessService.processEmailNotificationTemplate(
                emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams()))
                .thenReturn(emailNotificationContent);

        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getAutoSender()).thenReturn(sendFrom);

        service.notifyRecipients(emailData, List.of(recipientEmail));

        Thread.sleep(100);

        Email expectedEmail = Email.builder()
                .from(sendFrom)
                .recipients(EmailRecipients.builder()
                        .to(List.of(recipientEmail))
                        .cc(Collections.emptyList())
                        .bcc(Collections.emptyList())
                        .build())
                .subject(emailNotificationContent.getSubject())
                .text(emailNotificationContent.getText())
                .attachments(emailData.getAttachments())
                .build();
        verify(sendEmailService, times(1)).sendMail(expectedEmail);
        verify(notificationTemplateProcessService, times(1)).processEmailNotificationTemplate(emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams());
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmail, times(1)).getAutoSender();
    }

    @Test
    void notifyRecipients_with_cc() throws InterruptedException, IOException {
        EmailData emailData = buildEmailData();
        String recipientEmail = "recipient@email";
        List<String> ccRecipients = List.of("cc1RecEmail@email", "cc2RecEmail@email");
        NotificationContent emailNotificationContent = NotificationContent.builder()
                .subject("subject")
                .text("text")
                .build();
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(notificationTemplateProcessService.processEmailNotificationTemplate(
                emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams()))
                .thenReturn(emailNotificationContent);

        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getAutoSender()).thenReturn(sendFrom);

        service.notifyRecipients(emailData, List.of(recipientEmail), ccRecipients);

        Thread.sleep(100);

        Email expectedEmail = Email.builder()
                .from(sendFrom)
                .recipients(EmailRecipients.builder()
                        .to(List.of(recipientEmail))
                        .cc(ccRecipients)
                        .bcc(Collections.emptyList())
                        .build())
                .subject(emailNotificationContent.getSubject())
                .text(emailNotificationContent.getText())
                .attachments(emailData.getAttachments())
                .build();
        verify(sendEmailService, times(1)).sendMail(expectedEmail);
        verify(notificationTemplateProcessService, times(1)).processEmailNotificationTemplate(emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams());
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmail, times(1)).getAutoSender();
    }

    @Test
    void notifyRecipients_with_cc_and_bcc() throws InterruptedException, IOException {
        EmailData emailData = buildEmailData();
        String recipientEmail = "recipient@email";
        List<String> ccRecipients = List.of("cc1RecEmail@email", "cc2RecEmail@email");
        List<String> bccRecipients = List.of("bcc1RecEmail@email", "bcc2RecEmail@email");
        NotificationContent emailNotificationContent = NotificationContent.builder()
                .subject("subject")
                .text("text")
                .build();
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(notificationTemplateProcessService.processEmailNotificationTemplate(
                emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams()))
                .thenReturn(emailNotificationContent);

        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getAutoSender()).thenReturn(sendFrom);

        service.notifyRecipients(emailData, List.of(recipientEmail), ccRecipients, bccRecipients);

        Thread.sleep(100);

        Email expectedEmail = Email.builder()
                .from(sendFrom)
                .recipients(EmailRecipients.builder()
                        .to(List.of(recipientEmail))
                        .cc(ccRecipients)
                        .bcc(bccRecipients)
                        .build())
                .subject(emailNotificationContent.getSubject())
                .text(emailNotificationContent.getText())
                .attachments(emailData.getAttachments())
                .build();
        verify(sendEmailService, times(1)).sendMail(expectedEmail);
        verify(notificationTemplateProcessService, times(1)).processEmailNotificationTemplate(emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams());
        verify(notificationProperties, times(1)).getEmail();
        verify(notificationEmail, times(1)).getAutoSender();
    }

    @Test
    void notifyRecipient_error() throws InterruptedException, IOException {
        EmailData emailData = buildEmailData();
        String recipientEmail = "recipient@email";

        when(notificationTemplateProcessService.processEmailNotificationTemplate(
                emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams()))
                .thenThrow(BusinessException.class);

        assertThrows(BusinessException.class, () -> service.notifyRecipient(emailData, recipientEmail));

        Thread.sleep(100);

        verify(notificationTemplateProcessService, times(1)).processEmailNotificationTemplate(emailData.getNotificationTemplateData().getTemplateName(),
                emailData.getNotificationTemplateData().getCompetentAuthority(),
                null,
                emailData.getNotificationTemplateData().getTemplateParams());
        verifyNoInteractions(sendEmailService, notificationProperties);
    }

    private EmailData buildEmailData() throws IOException {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");
        byte[] att1fileContent = Files.readAllBytes(sampleFilePath);
        return EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName("someTemplate")
                        .competentAuthority(competentAuthority)
                        .templateParams(Map.of("templateParam1", "templateParam1Val"))
                        .build())
                .attachments(Map.of("att1", att1fileContent))
                .build();
    }

}