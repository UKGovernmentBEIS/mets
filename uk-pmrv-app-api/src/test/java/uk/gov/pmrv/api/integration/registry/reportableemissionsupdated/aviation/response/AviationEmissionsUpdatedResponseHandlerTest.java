package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AviationEmissionsUpdatedResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final Integer TEST_REGISTRY_ID = 1234;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private AviationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private AviationEmissionsUpdatedResponseHandler service;

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
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(List.of(RegistryResponseErrorCode.ERROR_0803.getCode()))
                .outcome(RegistryResponseStatus.ERROR).build();

        Account account = AviationAccount.builder()
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(TEST_REGISTRY_ID)
                .build();

        when(accountQueryService.findAccountByRegistryId(TEST_REGISTRY_ID)).thenReturn(account);

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        service.handleResponse(event, TEST_CORRELATION_ID);

        verify(accountQueryService, times(1)).findAccountByRegistryId(TEST_REGISTRY_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(Collections.emptyList())
                .outcome(RegistryResponseStatus.ERROR).build();

        Account account = AviationAccount.builder()
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(TEST_REGISTRY_ID)
                .build();

        when(accountQueryService.findAccountByRegistryId(TEST_REGISTRY_ID)).thenReturn(account);

        service.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testPrepareAndSendEmailToRegulator() {
        AccountEmissionsUpdatedResponseEvent event = AccountEmissionsUpdatedResponseEvent.builder()
                .event(AccountEmissionsUpdatedRequestEvent.builder().registryId(TEST_REGISTRY_ID)
                        .reportingYear(Year.of(2024)).build())
                .errors(Collections.singletonList(
                        RegistryResponseErrorCode.ERROR_0803.getCode()))
                .outcome(RegistryResponseStatus.ERROR).build();

        Account account = AviationAccount.builder()
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(TEST_REGISTRY_ID)
                .build();

        when(accountQueryService.findAccountByRegistryId(TEST_REGISTRY_ID)).thenReturn(account);

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        service.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }
}
