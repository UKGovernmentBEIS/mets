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
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
    void notifyAuthority_whenErrorIs0200_thenNotifiesFordway() {
        final String emitterId = "EM00001";
        final NotifyErrorDTO notifyErrorDTO = buildNotifyErrorDTO(emitterId, "Installation", IntegrationEventError.ERROR_0200);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);

        service.notifyAuthority(notifyErrorDTO);

        verify(notificationEmailService, times(2)).notifyRecipient(emailDataCaptor.capture(), recipientCaptor.capture());
        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData notificationTemplateData = capturedEmailData.getNotificationTemplateData();
        Map<String, Object> templateParams = notificationTemplateData.getTemplateParams();

        assertThat(templateParams.get(PmrvEmailNotificationTemplateConstants.EMITTER_ID)).isEqualTo(emitterId);
    }


    @Test
    void notifyAuthority_whenErrorIs0203_thenNotifiesRegulatorWithEmitterIdAsName() {
        final String emitterId = "EM00002";
        final NotifyErrorDTO notifyErrorDTO = buildNotifyErrorDTO(emitterId, "Aviation", IntegrationEventError.ERROR_0203);

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


    private NotifyErrorDTO buildNotifyErrorDTO(String emitterId, String service, IntegrationEventError errorCode) {
        return NotifyErrorDTO.builder()
                .event(OperatorUpdateEvent.builder().emitterId(emitterId).regulator("EA").build())
                .outcome(OperatorUpdateEventOutcome.builder()
                        .errors(List.of(IntegrationEventErrorDetails.builder().error(errorCode).build()))
                        .build())
                .authority(CompetentAuthorityEnum.ENGLAND)
                .service(service)
                .correlationId("corr-12345")
                .build();
    }
}