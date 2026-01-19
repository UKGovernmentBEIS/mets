package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

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
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.response.InstallationAccountUpdatedResponseHandler;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class InstallationAccountUpdatedResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_REGISTRY_ID = "1234";

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private InstallationAccountUpdatedResponseHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyRegulator_SuccessfulProcessing() {
        AccountUpdatingEventOutcome event = AccountUpdatingEventOutcome.builder()
                .event(AccountUpdatingEvent.builder()
                        .accountDetails(getAccountDetailsMessage())
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .outcome(IntegrationEventOutcome.SUCCESS).build();
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors() {
        AccountUpdatingEventOutcome event = AccountUpdatingEventOutcome.builder()
                .event(AccountUpdatingEvent.builder()
                        .accountDetails(getAccountDetailsMessage())
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0311)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.getAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(Optional.ofNullable(account));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);

        verify(accountQueryService, times(1)).getAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID));
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        AccountUpdatingEventOutcome event = AccountUpdatingEventOutcome.builder()
                .event(AccountUpdatingEvent.builder()
                        .accountDetails(getAccountDetailsMessage())
                        .build())
                 .accountIdentifier(TEST_REGISTRY_ID)
                .errors(Collections.emptyList())
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
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
        AccountUpdatingEventOutcome event = AccountUpdatingEventOutcome.builder()
                .event(AccountUpdatingEvent.builder()
                        .accountDetails(getAccountDetailsMessage())
                        .build())
                .accountIdentifier(TEST_REGISTRY_ID)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0311)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.getAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(Optional.ofNullable(account));

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(event, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

    @Test
    void test_without_registry_id() {
        AccountUpdatingEventOutcome event = AccountUpdatingEventOutcome.builder()
                .event(AccountUpdatingEvent.builder()
                        .accountDetails(UpdateAccountDetailsMessage.builder()
                                .accountName("name")
                                .build())
                        .build())
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0311)
                        .errorMessage("errorMessage")
                        .build()))
                .outcome(IntegrationEventOutcome.ERROR).build();

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(accountQueryService.getAccountByRegistryId(1)).thenReturn(null);

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handleResponse(event, TEST_CORRELATION_ID)
        );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    private UpdateAccountDetailsMessage getAccountDetailsMessage(){
        return UpdateAccountDetailsMessage.builder()
                .registryId(TEST_REGISTRY_ID)
                .accountName("name")
                .build();
    }
}
