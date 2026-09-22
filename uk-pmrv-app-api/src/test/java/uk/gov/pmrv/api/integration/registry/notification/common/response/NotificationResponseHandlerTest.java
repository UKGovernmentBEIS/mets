package uk.gov.pmrv.api.integration.registry.notification.common.response;

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
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_REGISTRY_ID = "1234";
    private static final String TEST_EMAIL = "test-email@example.com";

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private NotificationResponseHandler handler;

    @Test
    void shouldDoNothingOnSuccess() {
        RegulatorNoticeEventOutcome event = buildEvent(IntegrationEventOutcome.SUCCESS, List.of(), TEST_REGISTRY_ID);

        handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION);

        verifyNoInteractions(installationAccountQueryService, notificationEmailService, emailProperties);
    }

    @Test
    void shouldDoNothingOnEmptyErrors() {
        RegulatorNoticeEventOutcome event = buildEvent(IntegrationEventOutcome.ERROR, List.of(), TEST_REGISTRY_ID);

        handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION);

        verifyNoInteractions(installationAccountQueryService, notificationEmailService, emailProperties);
    }

    @Test
    void shouldNotifyRegulatorForInfoErrors() {
        RegulatorNoticeEventOutcome event = buildEvent(
                IntegrationEventOutcome.ERROR,
                List.of(error(IntegrationEventError.ERROR_0601)),
                TEST_REGISTRY_ID
        );

        InstallationAccount account = installationAccount();
        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);
        when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), TEST_EMAIL));

        handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> captor = ArgumentCaptor.forClass(EmailData.class);
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService).notifyRecipient(captor.capture(), eq(TEST_EMAIL));

        PmrvEmailNotificationTemplateData templateData = captor.getValue().getNotificationTemplateData();
        assertThat(templateData.getTemplateName())
                .isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_NOTIFICATION_INFO_ERROR_TEMPLATE.getName());
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);
        assertThat(templateData.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
        assertThat(templateData.getTemplateParams()).containsEntry(
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM,
                NotifyRegistryUtils.INSTALLATION_SERVICE_KEY
        );
    }

    @Test
    void shouldNotifyRegulatorForActionErrors() {
        RegulatorNoticeEventOutcome event = buildEvent(
                IntegrationEventOutcome.ERROR,
                List.of(error(IntegrationEventError.ERROR_0603)),
                TEST_REGISTRY_ID
        );

        InstallationAccount account = installationAccount();
        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);
        when(emailProperties.getEmail()).thenReturn(Map.of(CompetentAuthorityEnum.ENGLAND.getCode(), TEST_EMAIL));

        handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> captor = ArgumentCaptor.forClass(EmailData.class);
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService).notifyRecipient(captor.capture(), eq(TEST_EMAIL));

        PmrvEmailNotificationTemplateData templateData = captor.getValue().getNotificationTemplateData();
        assertThat(templateData.getTemplateName())
                .isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_NOTIFICATION_ERROR_TEMPLATE.getName());
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);
        assertThat(templateData.getCompetentAuthority()).isEqualTo(CompetentAuthorityEnum.ENGLAND);
        assertThat(templateData.getTemplateParams()).containsEntry(
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM,
                NotifyRegistryUtils.INSTALLATION_SERVICE_KEY
        );
    }

    @Test
    void shouldThrowWhenRegistryIdMissing() {
        RegulatorNoticeEventOutcome event = buildEvent(
                IntegrationEventOutcome.ERROR,
                List.of(error(IntegrationEventError.ERROR_0603)),
                null
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verifyNoInteractions(installationAccountQueryService, notificationEmailService, emailProperties);
    }

    @Test
    void shouldPropagateMultipleLiveAccountsFound() {
        RegulatorNoticeEventOutcome event = buildEvent(
                IntegrationEventOutcome.ERROR,
                List.of(error(IntegrationEventError.ERROR_0603)),
                TEST_REGISTRY_ID
        );

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID)))
                .thenThrow(new BusinessException(
                        MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND,
                        "More than one LIVE account found with registryId " + TEST_REGISTRY_ID
                ));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.INSTALLATION)
        );

        assertThat(exception.getErrorCode()).isEqualTo(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND);
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
    }

    private static RegulatorNoticeEventOutcome buildEvent(IntegrationEventOutcome outcome,
                                                          List<IntegrationEventErrorDetails> errors,
                                                          String registryId) {
        return RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder().registryId(registryId).build())
                .outcome(outcome)
                .errors(errors)
                .build();
    }

    private static IntegrationEventErrorDetails error(IntegrationEventError error) {
        return IntegrationEventErrorDetails.builder()
                .error(error)
                .errorMessage(error.getMessage())
                .build();
    }

    private static InstallationAccount installationAccount() {
        return InstallationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .accountType(AccountType.INSTALLATION)
                .status(InstallationAccountStatus.LIVE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .commencementDate(LocalDate.of(2024, 1, 1))
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();
    }
}