package uk.gov.pmrv.api.integration.registry.exemptstatus.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response.AviationAccountExemptResponseHandler;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.time.Year;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AviationAccountExemptResponseHandlerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-id";
    private static final String TEST_REGISTRY_ID = "1234";

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private AviationRegistryIntegrationEmailProperties emailProperties;

    @InjectMocks
    private AviationAccountExemptResponseHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifyRegulator_SuccessfulProcessing() {
        AccountExemptionUpdateEvent aviationEvent = AccountExemptionUpdateEvent.builder()
                .registryId(Long.valueOf(TEST_REGISTRY_ID))
                .exemptionFlag(true)
                .reportingYear(Year.of(2025))
                .build();

        AccountExemptionUpdateEventOutcome eventOutcome = AccountExemptionUpdateEventOutcome.builder()
                .event(aviationEvent)
                .outcome(IntegrationEventOutcome.SUCCESS)
                .build();

        handler.handleResponse(eventOutcome, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testNotifyRegulator_FailureWithSpecificErrors_NoRegistryIdInEvent() {
        AccountExemptionUpdateEvent aviationEvent = AccountExemptionUpdateEvent.builder()
                .exemptionFlag(true)
                .reportingYear(Year.of(2025))
                .build();

        AccountExemptionUpdateEventOutcome eventOutcome = AccountExemptionUpdateEventOutcome.builder()
                .event(aviationEvent)
                .outcome(IntegrationEventOutcome.SUCCESS)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0405)
                        .errorMessage("errorMessage")
                        .build()))
                .build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.findAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(eventOutcome, TEST_CORRELATION_ID);
    }

    @Test
    void testNotifyRegulator_GenericFailure_NoNotification() {
        AccountExemptionUpdateEvent aviationEvent = AccountExemptionUpdateEvent.builder()
                .registryId(Long.valueOf(TEST_REGISTRY_ID))
                .exemptionFlag(true)
                .reportingYear(Year.of(2025))
                .build();

        AccountExemptionUpdateEventOutcome eventOutcome = AccountExemptionUpdateEventOutcome.builder()
                .event(aviationEvent)
                .outcome(IntegrationEventOutcome.SUCCESS)
                .errors(Collections.emptyList())
                .build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.findAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);

        handler.handleResponse(eventOutcome, TEST_CORRELATION_ID);
        verify(notificationEmailService, never()).notifyRecipient(any(), any());
        verify(accountQueryService, never()).findAccountByRegistryId(any());
    }

    @Test
    void testPrepareAndSendEmailToRegulator() {
        AccountExemptionUpdateEvent aviationEvent = AccountExemptionUpdateEvent.builder()
                .registryId(Long.valueOf(TEST_REGISTRY_ID))
                .exemptionFlag(true)
                .reportingYear(Year.of(2025))
                .build();

        AccountExemptionUpdateEventOutcome eventOutcome = AccountExemptionUpdateEventOutcome.builder()
                .event(aviationEvent)
                .outcome(IntegrationEventOutcome.ERROR)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0405)
                        .errorMessage("errorMessage")
                        .build()))
                .build();

        Account account = AviationAccount.builder()
                .name("name")
                .emitterId("EM-test-121")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .registryId(Integer.valueOf(TEST_REGISTRY_ID))
                .build();

        when(accountQueryService.findAccountByRegistryId(Integer.valueOf(TEST_REGISTRY_ID))).thenReturn(account);

        Map<String, String> mockEmailMap = new HashMap<>();
        mockEmailMap.put(CompetentAuthorityEnum.ENGLAND.getCode(), "test-email@example.com");

        when(emailProperties.getEmail()).thenReturn(mockEmailMap);
        handler.handleResponse(eventOutcome, TEST_CORRELATION_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(any(EmailData.class), eq("test-email@example.com"));
    }

}
