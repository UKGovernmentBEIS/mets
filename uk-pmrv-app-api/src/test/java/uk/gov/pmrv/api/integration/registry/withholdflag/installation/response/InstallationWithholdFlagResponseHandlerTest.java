package uk.gov.pmrv.api.integration.registry.withholdflag.installation.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationWithholdFlagResponseHandlerTest {

    @InjectMocks
    private InstallationWithholdFlagResponseHandler handler;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Test
    void handleResponse_success() {
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.SUCCESS)
                .build();
        String correlationId = "correlationId";

        handler.handleResponse(eventOutcome, correlationId);

        verify(accountQueryService, never()).getAccountByRegistryId(any(Integer.class));
        verify(notificationEmailService, never()).notifyRecipient(any(), anyString());
    }

    @Test
    void handleResponse_error_no_errors_list() {
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(123L).build())
                .errors(new ArrayList<>())
                .build();
        String correlationId = "correlationId";

        handler.handleResponse(eventOutcome, correlationId);

        verify(accountQueryService, never()).getAccountByRegistryId(any(Integer.class));
        verify(notificationEmailService, never()).notifyRecipient(any(), anyString());
    }

    @Test
    void handleResponse_error_with_info_errors() {
        Long registryId = 123L;
        String correlationId = "correlationId";
        String recipientEmail = "regulator@email.com";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        Account account = InstallationAccount.builder()
                .registryId(registryId.intValue())
                .competentAuthority(ca)
                .accountType(AccountType.INSTALLATION)
                .emitterId("EMITTER_ID")
                .name("Account Name")
                .build();

        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0500)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(registryId).build())
                .errors(List.of(errorDetails))
                .build();

        when(accountQueryService.getAccountByRegistryId(registryId.intValue())).thenReturn(Optional.of(account));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(ca.getCode(), recipientEmail));

        handler.handleResponse(eventOutcome, correlationId);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), anyString());

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData templateData = capturedEmailData.getNotificationTemplateData();

        assertThat(templateData.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_INFO_TEMPLATE.getName());
        assertThat(templateData.getCompetentAuthority()).isEqualTo(ca);
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);
        
        Map<String, Object> templateParams = templateData.getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId());
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName());
        assertThat(templateParams).containsKey(PmrvEmailNotificationTemplateConstants.ERRORS);
        
        Map<String, String> errors = (Map<String, String>) templateParams.get(PmrvEmailNotificationTemplateConstants.ERRORS);
        assertThat(errors).containsKey(IntegrationEventError.ERROR_0500.getCode());
    }

    @Test
    void handleResponse_error_with_action_errors() {
        Long registryId = 123L;
        String correlationId = "correlationId";
        String recipientEmail = "regulator@email.com";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        Account account = InstallationAccount.builder()
                .registryId(registryId.intValue())
                .competentAuthority(ca)
                .accountType(AccountType.INSTALLATION)
                .emitterId("EMITTER_ID")
                .name("Account Name")
                .build();

        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0503)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(registryId).build())
                .errors(List.of(errorDetails))
                .build();

        when(accountQueryService.getAccountByRegistryId(registryId.intValue())).thenReturn(Optional.of(account));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(ca.getCode(), recipientEmail));

        handler.handleResponse(eventOutcome, correlationId);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), anyString());

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData templateData = capturedEmailData.getNotificationTemplateData();

        assertThat(templateData.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_ACTION_TEMPLATE.getName());
        assertThat(templateData.getCompetentAuthority()).isEqualTo(ca);
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);

        Map<String, Object> templateParams = templateData.getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId());
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName());
        assertThat(templateParams).containsKey(PmrvEmailNotificationTemplateConstants.ERRORS);

        Map<String, String> errors = (Map<String, String>) templateParams.get(PmrvEmailNotificationTemplateConstants.ERRORS);
        assertThat(errors).containsKey(IntegrationEventError.ERROR_0503.getCode());
    }

    @Test
    void handleResponse_error_account_not_found() {
        Long registryId = 123L;
        String correlationId = "correlationId";
        
        IntegrationEventErrorDetails errorDetails = IntegrationEventErrorDetails.builder()
                .error(IntegrationEventError.ERROR_0500)
                .build();
        AccountWithholdUpdateEventOutcome eventOutcome = AccountWithholdUpdateEventOutcome.builder()
                .outcome(IntegrationEventOutcome.ERROR)
                .event(AccountWithholdUpdateEvent.builder().registryId(registryId).build())
                .errors(List.of(errorDetails))
                .build();

        when(accountQueryService.getAccountByRegistryId(registryId.intValue())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> handler.handleResponse(eventOutcome, correlationId));
        
        verify(accountQueryService, times(1)).getAccountByRegistryId(registryId.intValue());
        verify(notificationEmailService, never()).notifyRecipient(any(), anyString());
    }
}
