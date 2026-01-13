package uk.gov.pmrv.api.notification.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.notificationapi.domain.NotificationContent;
import uk.gov.netz.api.notificationapi.system.SendSystemNotificationService;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemNotificationProcessAndSendServiceTest {

    @InjectMocks
    private SystemNotificationProcessAndSendService systemMessageNotificationService;

    @Mock
    private SendSystemNotificationService sendSystemNotificationService;

    @Mock
    private NotificationTemplateProcessService notificationTemplateProcessService;

    @Test
    void processAndSend() {
        final String messageSubject = "message_subject";
        final String messageText = "message_text";
        final String receiver = "receiver";
        final Long accountId = 1L;
        SystemNotificationInfo msgInfo = SystemNotificationInfo.builder()
            .template("template")
            .accountId(accountId)
            .receiver(receiver)
            .parameters(Map.of("param1", "val"))
            .build();

        NotificationContent notificationContent = NotificationContent.builder()
            .text(messageText)
            .subject(messageSubject)
            .build();

        //mock
        when(notificationTemplateProcessService.processMessageNotificationTemplate(msgInfo.getTemplate(), msgInfo.getParameters()))
            .thenReturn(notificationContent);

        //invoke
        systemMessageNotificationService.processAndSend(msgInfo);

        //verify
        verify(notificationTemplateProcessService, times(1))
            .processMessageNotificationTemplate(msgInfo.getTemplate(), msgInfo.getParameters());
        verify(sendSystemNotificationService, times(1))
            .send(msgInfo, notificationContent);
    }

}