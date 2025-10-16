package uk.gov.pmrv.api.integration.registry.setoperator.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorIdErrorNotifierServiceTest {

    @InjectMocks
    private OperatorIdErrorNotifierService service;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private AviationRegistryIntegrationEmailProperties aviationEmailProperties;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "errorHandleFordwayEmail", "test@fordway.com");
    }

    @Test
    void notifyAuthority_whenErrorIs0200AndAccountExists_thenNotifiesFordway() {
        final String emitterId = "EM00001";
        final String accountName = "Test Account Name";
        final NotifyErrorDTO notifyErrorDTO = buildNotifyErrorDTO(emitterId, "Installation", RegistryResponseErrorCode.ERROR_0200);
        final Account mockAccount = mock(Account.class);

        when(mockAccount.getName()).thenReturn(accountName);
        when(accountQueryService.getAccountByEmitterId(emitterId)).thenReturn(Optional.of(mockAccount));

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);

        service.notifyAuthority(notifyErrorDTO);

        verify(notificationEmailService, times(2)).notifyRecipient(emailDataCaptor.capture(), recipientCaptor.capture());
        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData notificationTemplateData = capturedEmailData.getNotificationTemplateData();
        Map<String, Object> templateParams = notificationTemplateData.getTemplateParams();

        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME)).isEqualTo(accountName);
        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.EMITTER_ID)).isEqualTo(emitterId);
    }

    @Test
    void notifyAuthority_whenErrorIs0201AndAccountDoesNotExist_thenNotifiesFordwayWithEmitterIdAsName() {
        final String emitterId = "EM00002";
        final NotifyErrorDTO notifyErrorDTO = buildNotifyErrorDTO(emitterId, "Aviation", RegistryResponseErrorCode.ERROR_0201);

        when(accountQueryService.getAccountByEmitterId(emitterId)).thenReturn(Optional.empty());

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        service.notifyAuthority(notifyErrorDTO);

        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), org.mockito.Mockito.anyString());
        PmrvEmailNotificationTemplateData notificationTemplateData = emailDataCaptor.getValue().getNotificationTemplateData();
        Map<String, Object> templateParams = notificationTemplateData.getTemplateParams();

        assertThat(notificationTemplateData.getAccountType()).isEqualTo(AccountType.AVIATION);
        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME)).isEqualTo(emitterId);
        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.EMITTER_ID)).isEqualTo(emitterId);
    }

    @Test
    void notifyAuthority_whenErrorIs0203_thenNotifiesRegulatorWithEmitterIdAsName() {
        final String emitterId = "EM00002";
        final NotifyErrorDTO notifyErrorDTO = buildNotifyErrorDTO(emitterId, "Aviation", RegistryResponseErrorCode.ERROR_0203);

        when(accountQueryService.getAccountByEmitterId(emitterId)).thenReturn(Optional.of(AviationAccount.builder().build()));
        when(aviationEmailProperties.getEmail()).thenReturn(Map.of("EA","test"));

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);

        service.notifyAuthority(notifyErrorDTO);

        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), org.mockito.Mockito.anyString());
        PmrvEmailNotificationTemplateData notificationTemplateData = emailDataCaptor.getValue().getNotificationTemplateData();
        Map<String, Object> templateParams = notificationTemplateData.getTemplateParams();

        assertThat(notificationTemplateData.getAccountType()).isEqualTo(AccountType.AVIATION);
        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME)).isEqualTo( null);
        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.EMITTER_ID)).isEqualTo(emitterId);
    }


    private NotifyErrorDTO buildNotifyErrorDTO(String emitterId, String service, RegistryResponseErrorCode errorCode) {
        return NotifyErrorDTO.builder()
                .event(SetOperatorIdResponseEvent.builder().emitterId(emitterId).regulator(CompetentAuthorityEnum.ENGLAND).build())
                .outcome(SetOperatorIdEventOutcome.builder()
                        .errors(List.of(RegistryIntegrationEventError.builder().error(errorCode).build()))
                        .build())
                .service(service)
                .correlationId("corr-12345")
                .build();
    }
}