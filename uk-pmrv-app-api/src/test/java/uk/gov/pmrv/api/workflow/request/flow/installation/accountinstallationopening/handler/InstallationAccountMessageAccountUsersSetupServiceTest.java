package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.ACCOUNT_USERS_SETUP;

@ExtendWith(MockitoExtension.class)
class InstallationAccountMessageAccountUsersSetupServiceTest {

    @InjectMocks
    private InstallationAccountMessageAccountUsersSetupService installationAccountMessageAccountUsersSetupService;

    @Mock
    private RequestService requestService;

    @Mock
    private SystemMessageNotificationService systemMessageNotificationService;

    @Test
    void executeTest() {
        final String requestId = "1";
        final String assignee = "operId";
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .accountId(accountId)
            .competentAuthority(ca)
            .payload(InstallationAccountOpeningRequestPayload.builder().operatorAssignee(assignee).build())
            .build();
        final SystemMessageNotificationInfo msgInfo = SystemMessageNotificationInfo.builder()
            .messageType(ACCOUNT_USERS_SETUP)
            .messageParameters(Map.of("accountId", accountId))
            .accountId(accountId)
            .receiver(assignee)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        installationAccountMessageAccountUsersSetupService.execute(requestId);

        // Verify
        verify(systemMessageNotificationService, times(1)).generateAndSendNotificationSystemMessage(msgInfo);
    }
}
