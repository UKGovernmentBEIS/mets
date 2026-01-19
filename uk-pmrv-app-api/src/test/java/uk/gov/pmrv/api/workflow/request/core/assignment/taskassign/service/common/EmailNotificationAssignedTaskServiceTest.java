package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;

import java.util.Optional;

public class EmailNotificationAssignedTaskServiceTest {

    @InjectMocks
    private EmailNotificationAssignedTaskService emailNotificationAssignedTaskService;

    @Mock
    private NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private WebAppProperties webAppProperties;

    private static final String USER_ID = "userId";
    private static final String EMAIL = "email@example.com";
    private static final String HOME_PAGE = "https://www.example.com";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webAppProperties.getUrl()).thenReturn(HOME_PAGE);
    }

    @Test
    public void sendEmailToRecipient_shouldCallNotifyRecipient_whenUserIdNotNull() {
        Long accountId = 1L;
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail(EMAIL);

        when(userAuthService.getUserByUserId(USER_ID)).thenReturn(userInfoDTO);
        String requestRegulatorAssignee = "requestRegulatorAssignee";
        Request request = Request.builder()
                .accountId(1L)
                .payload(PermitIssuanceRequestPayload.builder()
                        .regulatorAssignee(requestRegulatorAssignee)
                        .build())
                .build();
        RequestTask requestTask = RequestTask.builder().request(request).type(INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT).build();
        when(accountQueryService.getAccountType(accountId)).thenReturn(AccountType.INSTALLATION);

        emailNotificationAssignedTaskService.sendEmailToRecipient(USER_ID, requestTask, RoleTypeConstants.REGULATOR);

        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class),
            eq(EMAIL));
    }
}
