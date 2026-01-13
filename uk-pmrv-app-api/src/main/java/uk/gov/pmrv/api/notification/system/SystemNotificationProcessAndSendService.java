package uk.gov.pmrv.api.notification.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.notificationapi.domain.NotificationContent;
import uk.gov.netz.api.notificationapi.system.SendSystemNotificationService;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

@Log4j2
@RequiredArgsConstructor
@Service
public class SystemNotificationProcessAndSendService {
	
    private final SendSystemNotificationService sendSystemNotificationService;
    private final NotificationTemplateProcessService notificationTemplateProcessService;

    @Transactional
    public void processAndSend(SystemNotificationInfo msgInfo) {
        NotificationContent notificationContent = process(msgInfo);
        sendSystemNotificationService.send(msgInfo, notificationContent);
    }

    private NotificationContent process(SystemNotificationInfo msgInfo) {
        return notificationTemplateProcessService
                .processMessageNotificationTemplate(msgInfo.getTemplate(), msgInfo.getParameters());
    }
}
