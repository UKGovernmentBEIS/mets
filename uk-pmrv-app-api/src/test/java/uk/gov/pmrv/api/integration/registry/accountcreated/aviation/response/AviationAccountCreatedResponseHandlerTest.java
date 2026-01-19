package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response;

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
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AviationAccountCreatedResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_REGISTRY_ID = "1234";

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private AviationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private AviationAccountCreatedResponseHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyRegulator_SuccessfulProcessing() {
        AccountOpeningEventOutcome event = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(getAccountCreatedRegistryDetails())
                        .build())
                .outcome(IntegrationEventOutcome.SUCCESS).accountIdentifier(TEST_REGISTRY_ID).build();
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors() {
        AccountOpeningEventOutcome event = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(getAccountCreatedRegistryDetails())
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                                .error(IntegrationEventError.ERROR_0111)
                                .errorMessage("errorMessage")
                                .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.getAccountByEmitterId("1")).thenReturn(Optional.ofNullable(account));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);

        verify(accountQueryService, times(1)).getAccountByEmitterId("1");
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        AccountOpeningEventOutcome event = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(getAccountCreatedRegistryDetails())
                        .build())
                .errors(Collections.emptyList())
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.findAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);

        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testPrepareAndSendEmailToRegulator() {
        AccountOpeningEventOutcome event = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(getAccountCreatedRegistryDetails())
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0111)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.getAccountByEmitterId("1")).thenReturn(Optional.ofNullable(account));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void test_without_emitter_id() {
        AccountDetailsMessage accountCreatedRegistryDetails = AccountDetailsMessage.builder()
                .accountName("name")
                .build();

        AccountOpeningEventOutcome event = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(accountCreatedRegistryDetails)
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0111)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(accountQueryService.getAccountByEmitterId("1")).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handleResponse(event, TEST_CORRELATION_ID)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    private AccountDetailsMessage getAccountCreatedRegistryDetails(){
        return AccountDetailsMessage.builder()
                .emitterId("1")
                .accountName("name")
                .build();
    }
}
