package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;

@ExtendWith(MockitoExtension.class)
public class OperatorRecallEmailNotificationHandlerTest {

    private OperatorRecallEmailNotificationHandler operatorRecallEmailNotificationHandler;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;

    @Mock
    private VerifierUserInfoService verifierUserInfoService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @BeforeEach
    void setUp() {
        operatorRecallEmailNotificationHandler =
                new OperatorRecallEmailNotificationHandler(
                        userAuthService,
                        accountQueryService,
                        installationAccountQueryService,
                        verifierAuthorityQueryService,
                        verifierUserInfoService,
                        notificationEmailService
                ) {
                    @Override
                    public String getType() {
                        return "TEST_TYPE";
                    }

                    @Override
                    public RequestType getRequestType() {
                        return RequestType.AER;
                    }
                };
    }

    @Test
    void sendRecallEmailNotification_installationAccount_notifyAssignedVerifier() {
        RequestTask requestTask = RequestTask.builder()
                .type(RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT)
                .assignee("verifierAssignee")
                .build();

        Request request = Request.builder()
                .id("REQ-1")
                .accountId(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .requestTasks(List.of(requestTask))
                .payload(AerRequestPayload.builder().verifierAssignee("verifierAssignee").build())
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("Account name")
                .legalEntity(LegalEntityDTO.builder().name("Operator").build())
                .verificationBodyId(1L)
                .build();

        UserInfoDTO verifierUser = UserInfoDTO.builder()
                .email("assigned@test.com")
                .build();

        when(accountQueryService.getAccountType(1L)).thenReturn(AccountType.INSTALLATION);
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(accountDTO);
        when(userAuthService.getUserByUserId("verifierAssignee")).thenReturn(verifierUser);

        operatorRecallEmailNotificationHandler.sendRecallEmailNotification(request);

        verify(notificationEmailService).notifyRecipient(any(), eq("assigned@test.com"));
        verifyNoInteractions(verifierAuthorityQueryService, verifierUserInfoService);
    }

    @Test
    void sendRecallEmailNotification_nonInstallationAccount_doNothing() {
        Request request = Request.builder()
                .accountId(1L)
                .build();

        when(accountQueryService.getAccountType(1L)).thenReturn(AccountType.AVIATION);

        operatorRecallEmailNotificationHandler.sendRecallEmailNotification(request);

        verify(notificationEmailService, never()).notifyRecipient(any(), any());
    }

    @Test
    void sendRecallEmailNotification_noVerificationBody_doNothing() {
        Request request = Request.builder()
                .id("REQ-1")
                .accountId(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("Account")
                .verificationBodyId(null)
                .build();

        when(accountQueryService.getAccountType(1L)).thenReturn(AccountType.INSTALLATION);
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(accountDTO);

        operatorRecallEmailNotificationHandler.sendRecallEmailNotification(request);

        verify(notificationEmailService, never()).notifyRecipient(any(), any());
    }

    @Test
    void getTemplateParams_withLegalEntity() {
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("Installation")
                .legalEntity(LegalEntityDTO.builder().name("Operator").build())
                .build();

        Map<String, Object> result = operatorRecallEmailNotificationHandler.getTemplateParams(
                        accountDTO,
                        "REQ-1",
                        "TYPE",
                        AccountType.INSTALLATION);

        assertThat(result)
                .containsEntry(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, "REQ-1")
                .containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, "Operator")
                .containsEntry(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, "Installation");
    }

    @Test
    void getTemplateParams_withoutLegalEntity() {
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("Installation")
                .legalEntity(null)
                .build();

        Map<String, Object> result = operatorRecallEmailNotificationHandler.getTemplateParams(
                        accountDTO,
                        "REQ-1",
                        "TYPE",
                        AccountType.INSTALLATION);

        assertThat(result).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, null);
    }

    @Test
    void sendRecallEmailNotification_installationAccount_notifyVerifierAdminWhenNoAssignedVerifier() {
        Request request = Request.builder()
                .id("REQ-1")
                .accountId(1L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .payload(AerRequestPayload.builder().build())
                .build();

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("Account name")
                .legalEntity(LegalEntityDTO.builder().name("Operator").build())
                .verificationBodyId(1L)
                .build();

        UserInfoDTO verifierUser = UserInfoDTO.builder()
                .email("verifier@test.com")
                .build();

        when(accountQueryService.getAccountType(1L)).thenReturn(AccountType.INSTALLATION);
        when(installationAccountQueryService.getAccountDTOById(1L)).thenReturn(accountDTO);
        when(verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(1L)).thenReturn(List.of("verifier-admin"));
        when(verifierUserInfoService.getVerifierUsersInfo(List.of("verifier-admin"))).thenReturn(List.of(verifierUser));

        operatorRecallEmailNotificationHandler.sendRecallEmailNotification(request);

        verify(notificationEmailService).notifyRecipient(any(), eq("verifier@test.com"));
        verify(verifierAuthorityQueryService).findVerifierAdminsByVerificationBody(1L);
        verify(verifierUserInfoService).getVerifierUsersInfo(List.of("verifier-admin"));
        verify(userAuthService, never()).getUserByUserId(any());
    }
}
