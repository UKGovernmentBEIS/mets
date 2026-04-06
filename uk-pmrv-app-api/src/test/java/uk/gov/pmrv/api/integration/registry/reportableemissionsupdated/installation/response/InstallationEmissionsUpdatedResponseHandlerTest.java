package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

import java.time.Year;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

public class InstallationEmissionsUpdatedResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final Integer TEST_REGISTRY_ID = 1234;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private InstallationEmissionsUpdatedResponseHandler service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyRegulator_SuccessfulProcessing() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(1212).build())
                .outcome(RegistryResponseStatus.SUCCESS).build();
        service.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(installationAccountQueryService, never()).getSingleLiveAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(List.of(RegistryResponseErrorCode.ERROR_0803.getCode()))
                .outcome(RegistryResponseStatus.ERROR).build();

        List<InstallationAccount> accounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(accounts.get(0));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        service.handleResponse(event, TEST_CORRELATION_ID);

        verify(installationAccountQueryService, times(1)).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(Collections.emptyList())
                .outcome(RegistryResponseStatus.ERROR).build();

        List<InstallationAccount> accounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(accounts.get(0));

        service.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(installationAccountQueryService, never()).getSingleLiveAccountByRegistryId(any());
    }

    @Test
    void testPrepareAndSendEmailToRegulator() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(Collections.singletonList(
                        RegistryResponseErrorCode.ERROR_0803.getCode()))
                .outcome(RegistryResponseStatus.ERROR).build();

        List<InstallationAccount> accounts = getInstallationAccounts();

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(accounts.get(0));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        service.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void handleResponse_multiple_live_accounts_found() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(Collections.singletonList(
                        RegistryResponseErrorCode.ERROR_0803.getCode()))
                .outcome(RegistryResponseStatus.ERROR).build();

        List<InstallationAccount> installationAccounts = getInstallationAccounts();
        installationAccounts.get(1).setStatus(InstallationAccountStatus.LIVE);

        when(installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID)))
                .thenThrow(new BusinessException(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, "More than one LIVE account found with registryId " + TEST_REGISTRY_ID));

        BusinessException be = Assertions.assertThrows(BusinessException.class, () -> service.handleResponse(event, TEST_CORRELATION_ID));

        assertEquals(MetsErrorCode.MULTIPLE_LIVE_ACCOUNTS_FOUND, be.getErrorCode());
        verify(installationAccountQueryService).getSingleLiveAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verifyNoInteractions(notificationEmailService);
    }

    InstallationAccount buildAccount(InstallationAccountStatus status) {
        return InstallationAccount.builder()
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(status)
                .registryId(TEST_REGISTRY_ID)
                .build();
    }

    List<InstallationAccount> getInstallationAccounts() {
        InstallationAccount account = buildAccount(InstallationAccountStatus.LIVE);
        InstallationAccount account2 = buildAccount(InstallationAccountStatus.NEW);
        return List.of(account, account2);
    }
}
