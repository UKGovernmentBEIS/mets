package uk.gov.pmrv.api.integration.registry.notification.installation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.notification.installation.response.InstallationNotificationResponseHandler;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

public class InstallationNotificationResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_REGISTRY_ID = "1234";

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private InstallationNotificationResponseHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyRegulator_SuccessfulProcessing() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId(TEST_REGISTRY_ID)
                        .build())
                .outcome(IntegrationEventOutcome.SUCCESS)
                .build();
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(installationAccountQueryService, never()).getSingleLiveAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId(TEST_REGISTRY_ID)
                        .build())
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0601)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        List<InstallationAccount> installationAccounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(installationAccounts.get(0));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);

        verify(installationAccountQueryService, times(1)).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId(TEST_REGISTRY_ID)
                        .build())
                .errors(Collections.emptyList())
                .outcome(IntegrationEventOutcome.ERROR).build();

        List<InstallationAccount> installationAccounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(installationAccounts.get(0));

        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(installationAccountQueryService, never()).getSingleLiveAccountByRegistryId(any());
    }

    @Test
    void testPrepareAndSendEmailToRegulator() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId(TEST_REGISTRY_ID)
                        .build())
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0601)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        List<InstallationAccount> installationAccounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(installationAccounts.get(0));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void test_without_registry_id() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .build())
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0603)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(1)).thenReturn(null);

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handleResponse(event, TEST_CORRELATION_ID)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void handleResponse_multiple_live_accounts_found() {
        RegulatorNoticeEventOutcome event = RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId(TEST_REGISTRY_ID)
                        .build())
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0603)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        List<InstallationAccount> installationAccounts = getInstallationAccounts();
        installationAccounts.get(1).setStatus(InstallationAccountStatus.LIVE);

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID)))
                .thenThrow(new BusinessException(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, "More than one LIVE account found with registryId " + TEST_REGISTRY_ID));

        BusinessException be = Assertions.assertThrows(BusinessException.class, () -> handler.handleResponse(event, TEST_CORRELATION_ID));

        assertEquals(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, be.getErrorCode());
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verifyNoInteractions(notificationEmailService);
    }

    public List<InstallationAccount> getInstallationAccounts() {
        InstallationAccount account = InstallationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .status(InstallationAccountStatus.LIVE)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();
        InstallationAccount account2 = InstallationAccount.builder()
                .name("name2")
                .emitterId("EM-test-122")
                .status(InstallationAccountStatus.NEW)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();
        return List.of(account, account2);
    }
}
