package uk.gov.pmrv.api.notification.mail.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.Email;
import uk.gov.netz.api.notificationapi.mail.domain.EmailRecipients;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PmrvJavaSendEmailServiceImplTest {

    @InjectMocks
    private PmrvJavaSendEmailServiceImpl sendMailService;

    @Mock
    private JavaMailSender javaMailSender;
    
    @Mock
    private NotificationProperties notificationProperties;

    @Test
    void sendMail() throws MessagingException, IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");
        byte[] att1fileContent = Files.readAllBytes(sampleFilePath);
        
        Email email = Email.builder()
                .from("sender@email")
                .recipients(EmailRecipients.builder()
                		.to(List.of("receiver@email"))
                		.cc(List.of("cc@email"))
                		.bcc(List.of("bcc@email"))
                		.build())
                .subject("mail subject")
                .text("mail text")
                .attachments(Map.of("att1", att1fileContent))
                .build();
        MimeMessage message = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        NotificationProperties.SmtpHeaders smtpHeaders = new NotificationProperties.SmtpHeaders();
        smtpHeaders.setEmailOriginator("pmrv");
        when(notificationProperties.getSmtpHeaders()).thenReturn(smtpHeaders);
        
        sendMailService.sendMail(email);

        verify(notificationProperties, times(3)).getSmtpHeaders();
        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        MimeMessage messageCaptured = messageCaptor.getValue();
        
        assertThat(messageCaptured.getFrom()).isEqualTo(new InternetAddress[] {new InternetAddress("sender@email")});
        assertThat(messageCaptured.getRecipients(Message.RecipientType.TO)).isEqualTo(new InternetAddress[] {new InternetAddress("receiver@email")});
        assertThat(messageCaptured.getRecipients(Message.RecipientType.CC)).isEqualTo(new InternetAddress[] {new InternetAddress("cc@email")});
        assertThat(messageCaptured.getRecipients(Message.RecipientType.BCC)).isEqualTo(new InternetAddress[] {new InternetAddress("bcc@email")});
        assertThat(messageCaptured.getSubject()).isEqualTo(email.getSubject());
        assertThat(messageCaptured.getHeader("X-SES-CONFIGURATION-SET")).isEqualTo(new String[]{"pmrv"});
        
        String body = IOUtils.toString(MimeUtility.decode(message.getInputStream(), "quoted-printable"), "UTF-8");
        assertThat(body).contains(email.getText());
        
        MimeMultipart multiPart = (MimeMultipart) message.getContent();
        int countAttachments = 0;
        for (int i = 0; i < multiPart.getCount(); i++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
            if(Part.ATTACHMENT.equals(part.getDisposition())) {
                countAttachments++;
            }
        }
        assertThat(countAttachments).isEqualTo(1);
    }

}
