package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.notification.system.SystemNotificationProcessAndSendService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstallationAccountMessageAccountUsersSetupService {
	
    private final SystemNotificationProcessAndSendService systemMessageNotificationService;
    private final RequestService requestService;

    public void execute(String requestId) {

        Request request = requestService.findRequestById(requestId);
        Long accountId = request.getAccountId();

        SystemNotificationInfo msgInfo = SystemNotificationInfo.builder()
        	.template(PmrvNotificationTemplateName.ACCOUNT_USERS_SETUP.getName())
            .parameters(Map.of("accountId", accountId))
            .accountId(accountId)
            .receiver(request.getPayload().getOperatorAssignee())
            .build();

        systemMessageNotificationService.processAndSend(msgInfo);
    }
}
