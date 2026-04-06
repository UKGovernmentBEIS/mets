package uk.gov.pmrv.api.integration.registry.withholdflag.installation.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class InstallationWithholdFlagResponseHandlerTest {

    private static final Long REGISTRY_ID = 123L;
    private static final String CORRELATION_ID = "correlationId";
    private static final String EMAIL_RECIPIENT = "regulator@email.com";
    private static final String EMITTER_ID = "EM00001";
    private static final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

    @InjectMocks
    private InstallationWithholdFlagResponseHandler handler;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Test
    void handleResponse_success() {
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.SUCCESS)
                .build();

        handler.handleResponse(eventOutcome, CORRELATION_ID);

        verify(installationAccountQueryService, never()).getAccountsByRegistryId(any(Integer.class));
        verify(notificationEmailService, never()).notifyRecipient(any(), anyString());
    }

    @Test
    void handleResponse_error_no_errors_list() {
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(REGISTRY_ID).build())
                .errors(new ArrayList<>())
                .build();

        handler.handleResponse(eventOutcome, CORRELATION_ID);

        verify(installationAccountQueryService, never()).getAccountsByRegistryId(any(Integer.class));
        verify(notificationEmailService, never()).notifyRecipient(any(), anyString());
    }

    @Test
    void handleResponse_error_with_info_errors() {
        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0500)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(REGISTRY_ID).build())
                .errors(List.of(errorDetails))
                .build();
        List<InstallationAccount> accounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Math.toIntExact(REGISTRY_ID))).thenReturn(accounts.get(0));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(ca.getCode(), EMAIL_RECIPIENT));

        handler.handleResponse(eventOutcome, CORRELATION_ID);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), anyString());

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData templateData = capturedEmailData.getNotificationTemplateData();

        assertThat(templateData.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_INFO_TEMPLATE.getName());
        assertThat(templateData.getCompetentAuthority()).isEqualTo(ca);
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);
        
        Map<String, Object> templateParams = templateData.getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, accounts.get(0).getEmitterId());
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, CORRELATION_ID);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accounts.get(0).getName());
        assertThat(templateParams).containsKey(PmrvEmailNotificationTemplateConstants.ERRORS);
        
        Map<String, String> errors = (Map<String, String>) templateParams.get(PmrvEmailNotificationTemplateConstants.ERRORS);
        assertThat(errors).containsKey(IntegrationEventError.ERROR_0500.getCode());
    }

    @Test
    void handleResponse_error_with_action_errors() {
        List<InstallationAccount> accounts = getInstallationAccounts();

        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0503)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(REGISTRY_ID).build())
                .errors(List.of(errorDetails))
                .build();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Math.toIntExact(REGISTRY_ID))).thenReturn(accounts.get(0));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(ca.getCode(), EMAIL_RECIPIENT));

        handler.handleResponse(eventOutcome, CORRELATION_ID);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), anyString());

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData templateData = capturedEmailData.getNotificationTemplateData();

        assertThat(templateData.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_ACTION_TEMPLATE.getName());
        assertThat(templateData.getCompetentAuthority()).isEqualTo(ca);
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);

        Map<String, Object> templateParams = templateData.getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, accounts.get(0).getEmitterId());
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, CORRELATION_ID);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accounts.get(0).getName());
        assertThat(templateParams).containsKey(PmrvEmailNotificationTemplateConstants.ERRORS);

        Map<String, String> errors = (Map<String, String>) templateParams.get(PmrvEmailNotificationTemplateConstants.ERRORS);
        assertThat(errors).containsKey(IntegrationEventError.ERROR_0503.getCode());
    }

    @Test
    void handleResponse_error_account_not_found() {
        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0500)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(REGISTRY_ID).build())
                .errors(List.of(errorDetails))
                .build();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Math.toIntExact(REGISTRY_ID)))
                .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No account found"));

        assertThrows(BusinessException.class, () -> handler.handleResponse(eventOutcome, CORRELATION_ID));

        verify(notificationEmailService, never()).notifyRecipient(any(), any());
    }

    @Test
    void handleResponse_multiple_live_accounts_found() {
        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0500)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(REGISTRY_ID).build())
                .errors(List.of(errorDetails))
                .build();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Math.toIntExact(REGISTRY_ID)))
                .thenThrow(new BusinessException(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, "More than one LIVE account found with registryId " + REGISTRY_ID));

        BusinessException be = Assertions.assertThrows(BusinessException.class, () ->  handler.handleResponse(eventOutcome, CORRELATION_ID));

        assertEquals(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, be.getErrorCode());
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(REGISTRY_ID.intValue());
        verifyNoInteractions(notificationEmailService);
    }

    private InstallationAccount buildAccount(InstallationAccountStatus status) {
        return InstallationAccount.builder()
                .registryId(Math.toIntExact(REGISTRY_ID))
                .emitterId(EMITTER_ID)
                .accountType(AccountType.INSTALLATION)
                .name("Account Name")
                .status(status)
                .competentAuthority(ca)
                .build();
    }

    public List<InstallationAccount> getInstallationAccounts() {
        InstallationAccount account = buildAccount(InstallationAccountStatus.LIVE);
        InstallationAccount account2 = buildAccount(InstallationAccountStatus.NEW);
        return List.of(account, account2);
    }
}
