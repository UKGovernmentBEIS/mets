package uk.gov.pmrv.api.integration.registry.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistryIdEmailNotifierServiceTest {

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @InjectMocks
    private RegistryIdEmailNotifierService service;

    @Test
    void registryIdNonExistenceNotifyRegulator() {
        Long accountId = 1L;
        String emitterId = "EM123456";
        String accountName = "Test Installation";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String regulatorEmail = "regulator@england.gov.uk";
        String templateName = PmrvNotificationTemplateName.REGISTRY_INTEGRATION_NOTIFICATION_MISSING_REGISTRY_ID.getName();
        String sourceSystem = NotifyRegistryUtils.INSTALLATION_SERVICE_KEY;

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .emitterId(emitterId)
                .name(accountName)
                .competentAuthority(competentAuthority)
                .build();

        when(installationEmailProperties.getEmail())
                .thenReturn(Map.of(competentAuthority.getCode(), regulatorEmail));

        service.registryIdNonExistenceNotifyRegulator(accountDTO, templateName, sourceSystem);

        ArgumentCaptor<EmailData<PmrvEmailNotificationTemplateData>> emailDataCaptor =
                ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService).notifyRecipient(emailDataCaptor.capture(), eq(regulatorEmail));

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        PmrvEmailNotificationTemplateData templateData = capturedEmailData.getNotificationTemplateData();

        assertThat(templateData.getTemplateName()).isEqualTo(templateName);
        assertThat(templateData.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(templateData.getAccountType()).isEqualTo(AccountType.INSTALLATION);
        assertThat(templateData.getTemplateParams())
                .containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId)
                .containsEntry(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, sourceSystem)
                .containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accountName);
    }
}

