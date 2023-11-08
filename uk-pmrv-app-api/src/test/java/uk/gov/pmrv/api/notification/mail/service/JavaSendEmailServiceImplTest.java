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

import uk.gov.pmrv.api.notification.mail.domain.Email;

@ExtendWith(MockitoExtension.class)
class JavaSendEmailServiceImplTest {

    @InjectMocks
    private JavaSendEmailServiceImpl sendMailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendMail() throws MessagingException, IOException {
        Path sampleFilePath = Paths.get("src", "test", "resources", "files", "sample.pdf");
        byte[] att1fileContent = Files.readAllBytes(sampleFilePath);
        
        Email email = Email.builder()
                .sendFrom("sender@email")
                .sendTo(List.of("receiver@email"))
                .sendCc(List.of("cc@email"))
                .subject("mail subject")
                .text("mail text")
                .attachments(Map.of("att1", att1fileContent))
                .build();
        MimeMessage message = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        sendMailService.sendMail(email);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());
        MimeMessage messageCaptured = messageCaptor.getValue();
        
        assertThat(messageCaptured.getFrom()).isEqualTo(new InternetAddress[] {new InternetAddress(email.getSendFrom())});
        assertThat(messageCaptured.getRecipients(Message.RecipientType.TO)).isEqualTo(new InternetAddress[] {new InternetAddress("receiver@email")});
        assertThat(messageCaptured.getRecipients(Message.RecipientType.CC)).isEqualTo(new InternetAddress[] {new InternetAddress("cc@email")});
        assertThat(messageCaptured.getSubject()).isEqualTo(email.getSubject());
        
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