package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactResponseHandlerTest {

    private static final Integer REGISTRY_ID = 10;
    private static final String CORRELATION_ID = "corrId";
    private static final String EMAIL_RECIPIENT = "ca_recipient@test.com";
    private static final String EMITTER_ID = "EM00001";
    private static final CompetentAuthorityEnum CA_ID = CompetentAuthorityEnum.ENGLAND;
    private static final String INSTALLATION_SYSTEM_IDENTIFIER = NotifyRegistryUtils.INSTALLATION_SERVICE_KEY;
    private static final String AVIATION_SYSTEM_IDENTIFIER = NotifyRegistryUtils.AVIATION_SERVICE_KEY;

    private AccountContactResponseHandler accountContactResponseHandler;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @Mock
    private AviationRegistryIntegrationEmailProperties aviationEmailProperties;

    private Account account;
    private IntegrationEventErrorDetails infoErrorDetails;
    private IntegrationEventErrorDetails actionErrorDetails;
    private MetsContactsEventOutcome eventOutcome;


    @BeforeEach
    void setUp() {
        account = buildAccount();
        infoErrorDetails = buildErrorDetails(IntegrationEventError.ERROR_0700);
        actionErrorDetails = buildErrorDetails(IntegrationEventError.ERROR_0703);

        // Manual instantiation to ensure correct mock passing (no InjectMocks ambiguity)
        accountContactResponseHandler = new AccountContactResponseHandler(
                accountQueryService,
                notificationEmailService,
                installationEmailProperties,
                aviationEmailProperties
        );
    }

    @Test
    void handleResponse_ok_outcome_no_interaction() {
        MetsContactsEventOutcome eventOutcome = buildMetsContactsEventOutcome(IntegrationEventOutcome.SUCCESS, List.of());

        accountContactResponseHandler.handleResponse(eventOutcome, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER);

        verifyNoInteractions(accountQueryService, notificationEmailService, installationEmailProperties, aviationEmailProperties);
    }

    @Test
    void handleResponse_error_outcome_empty_errors_no_email_sent() {
        MetsContactsEventOutcome eventOutcome = buildMetsContactsEventOutcome(IntegrationEventOutcome.ERROR, List.of());

        accountContactResponseHandler.handleResponse(eventOutcome, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER);

        verifyNoInteractions(accountQueryService, notificationEmailService);
    }

    @Test
    void handleResponse_error_outcome_sends_two_emails_for_installation_service() {
        MetsContactsEventOutcome eventOutcomeWithErrors = buildMetsContactsEventOutcome(List.of(infoErrorDetails, actionErrorDetails));

        when(accountQueryService.getAccountByRegistryId(REGISTRY_ID)).thenReturn(Optional.of(account));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(CA_ID.getCode(), EMAIL_RECIPIENT));

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        accountContactResponseHandler.handleResponse(eventOutcomeWithErrors, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER);

        verify(accountQueryService, times(2)).getAccountByRegistryId(REGISTRY_ID);
        verify(notificationEmailService, times(2)).notifyRecipient(emailDataCaptor.capture(), eq(EMAIL_RECIPIENT));

        List<EmailData> capturedEmails = emailDataCaptor.getAllValues();

        EmailData actionEmail = capturedEmails.stream()
                .filter(ed -> ed.getNotificationTemplateData().getTemplateName().equals(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_ACTION_TEMPLATE.getName()))
                .findFirst().orElseThrow();

        Map<String, Object> params = actionEmail.getNotificationTemplateData().getTemplateParams();
        assertEquals(INSTALLATION_SYSTEM_IDENTIFIER, params.get(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM));

        verify(installationEmailProperties, times(2)).getEmail();
        verifyNoInteractions(aviationEmailProperties);
    }

    @Test
    void handleResponse_error_outcome_sends_two_emails_for_aviation_service() {
        MetsContactsEventOutcome eventOutcomeWithErrors = buildMetsContactsEventOutcome(List.of(infoErrorDetails, actionErrorDetails));

        when(accountQueryService.getAccountByRegistryId(REGISTRY_ID)).thenReturn(Optional.of(account));
        when(aviationEmailProperties.getEmail()).thenReturn(Map.of(CA_ID.getCode(), EMAIL_RECIPIENT));

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        accountContactResponseHandler.handleResponse(eventOutcomeWithErrors, CORRELATION_ID, AVIATION_SYSTEM_IDENTIFIER);

        verify(accountQueryService, times(2)).getAccountByRegistryId(REGISTRY_ID);
        verify(notificationEmailService, times(2)).notifyRecipient(emailDataCaptor.capture(), eq(EMAIL_RECIPIENT));

        List<EmailData> capturedEmails = emailDataCaptor.getAllValues();

        EmailData actionEmail = capturedEmails.stream()
                .filter(ed -> ed.getNotificationTemplateData().getTemplateName().equals(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_ACTION_TEMPLATE.getName()))
                .findFirst().orElseThrow();

        Map<String, Object> params = actionEmail.getNotificationTemplateData().getTemplateParams();
        assertEquals(AVIATION_SYSTEM_IDENTIFIER, params.get(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM));

        verify(aviationEmailProperties, times(2)).getEmail();
        verifyNoInteractions(installationEmailProperties);
    }

    @Test
    void handleResponse_error_outcome_throws_exception_if_account_not_found() {
        MetsContactsEventOutcome eventOutcomeWithErrors = buildMetsContactsEventOutcome(List.of(infoErrorDetails, actionErrorDetails));

        when(accountQueryService.getAccountByRegistryId(REGISTRY_ID)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                accountContactResponseHandler.handleResponse(eventOutcomeWithErrors, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER));

        verify(accountQueryService).getAccountByRegistryId(REGISTRY_ID);
        verifyNoInteractions(notificationEmailService);
    }

    @Test
    void handleResponse_sends_only_one_email_if_only_action_error_present() {
        MetsContactsEventOutcome eventOutcome = buildMetsContactsEventOutcome(List.of(actionErrorDetails));

        when(accountQueryService.getAccountByRegistryId(REGISTRY_ID)).thenReturn(Optional.of(account));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(CA_ID.getCode(), EMAIL_RECIPIENT));

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        accountContactResponseHandler.handleResponse(eventOutcome, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER);

        verify(accountQueryService, times(1)).getAccountByRegistryId(REGISTRY_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), eq(EMAIL_RECIPIENT));

        EmailData email = emailDataCaptor.getValue();
        assertEquals(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_ACTION_TEMPLATE.getName(),
                email.getNotificationTemplateData().getTemplateName());
    }

    @Test
    void handleResponse_sends_only_one_email_if_only_info_error_present() {
        MetsContactsEventOutcome eventOutcome = buildMetsContactsEventOutcome(List.of(infoErrorDetails));

        when(accountQueryService.getAccountByRegistryId(REGISTRY_ID)).thenReturn(Optional.of(account));
        when(installationEmailProperties.getEmail()).thenReturn(Map.of(CA_ID.getCode(), EMAIL_RECIPIENT));

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        accountContactResponseHandler.handleResponse(eventOutcome, CORRELATION_ID, INSTALLATION_SYSTEM_IDENTIFIER);

        verify(accountQueryService, times(1)).getAccountByRegistryId(REGISTRY_ID);
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), eq(EMAIL_RECIPIENT));

        EmailData email = emailDataCaptor.getValue();
        assertEquals(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_INFO_TEMPLATE.getName(),
                email.getNotificationTemplateData().getTemplateName());
    }

    private Account buildAccount() {
        return InstallationAccount.builder()
                .registryId(REGISTRY_ID)
                .emitterId(EMITTER_ID)
                .name("Account Name")
                .competentAuthority(CA_ID)
                .build();
    }

    private MetsContactsEventOutcome buildMetsContactsEventOutcome(IntegrationEventOutcome outcome, List<IntegrationEventErrorDetails> errors) {
        return MetsContactsEventOutcome.builder()
                .accountIdentifier(String.valueOf(REGISTRY_ID))
                .outcome(outcome)
                .errors(errors)
                .event(MetsContactsEvent.builder().operatorId("1").build())
                .build();
    }

    private MetsContactsEventOutcome buildMetsContactsEventOutcome(List<IntegrationEventErrorDetails> errors) {
        return buildMetsContactsEventOutcome(IntegrationEventOutcome.ERROR, errors);
    }

    private IntegrationEventErrorDetails buildErrorDetails(IntegrationEventError error) {
        return IntegrationEventErrorDetails.builder()
                .error(error)
                .build();
    }
}