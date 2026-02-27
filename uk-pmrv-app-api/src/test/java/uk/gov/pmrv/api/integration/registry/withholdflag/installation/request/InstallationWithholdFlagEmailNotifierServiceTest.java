package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationWithholdFlagEmailNotifierServiceTest {

    @Mock
    private NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;

    @Mock
    private InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @InjectMocks
    private InstallationWithholdFlagEmailNotifierService service;

    @Test
    void registryIdNonExistenceNotifyRegulator() {
        Long accountId = 1L;
        String emitterId = "EM123456";
        String accountName = "Test Installation Account";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String regulatorEmail = "regulator@england.gov.uk";

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .emitterId(emitterId)
                .name(accountName)
                .competentAuthority(competentAuthority)
                .build();

        Map<String, String> emailMap = Map.of(competentAuthority.getCode(), regulatorEmail);
        when(installationEmailProperties.getEmail()).thenReturn(emailMap);

        service.registryIdNonExistenceNotifyRegulator(accountDTO);

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        ArgumentCaptor<String> emailAddressCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationEmailService, times(1)).notifyRecipient(
                emailDataCaptor.capture(),
                emailAddressCaptor.capture()
        );

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        String capturedEmailAddress = emailAddressCaptor.getValue();

        assertThat(capturedEmailAddress).isEqualTo(regulatorEmail);
        assertThat(capturedEmailData.getNotificationTemplateData()).isNotNull();
        assertThat(capturedEmailData.getNotificationTemplateData().getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(capturedEmailData.getNotificationTemplateData().getTemplateName())
                .isEqualTo(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_WITHHOLD_FLAG_MISSING_REGISTRY_ID.getName());
        assertThat(capturedEmailData.getNotificationTemplateData().getAccountType()).isEqualTo(AccountType.INSTALLATION);

        Map<String, Object> templateParams = capturedEmailData.getNotificationTemplateData().getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, "Installation");
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accountName);

        verify(installationEmailProperties, times(1)).getEmail();
    }

    @Test
    void registryIdNonExistenceNotifyRegulator_withDifferentCompetentAuthority() {
        Long accountId = 2L;
        String emitterId = "EM789012";
        String accountName = "Another Test Installation Account";
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String regulatorEmail = "regulator@scotland.gov.uk";

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .emitterId(emitterId)
                .name(accountName)
                .competentAuthority(competentAuthority)
                .build();

        Map<String, String> emailMap = Map.of(competentAuthority.getCode(), regulatorEmail);
        when(installationEmailProperties.getEmail()).thenReturn(emailMap);

        service.registryIdNonExistenceNotifyRegulator(accountDTO);

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        ArgumentCaptor<String> emailAddressCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationEmailService, times(1)).notifyRecipient(
                emailDataCaptor.capture(),
                emailAddressCaptor.capture()
        );

        EmailData<PmrvEmailNotificationTemplateData> capturedEmailData = emailDataCaptor.getValue();
        String capturedEmailAddress = emailAddressCaptor.getValue();

        assertThat(capturedEmailAddress).isEqualTo(regulatorEmail);
        assertThat(capturedEmailData.getNotificationTemplateData().getCompetentAuthority()).isEqualTo(competentAuthority);

        Map<String, Object> templateParams = capturedEmailData.getNotificationTemplateData().getTemplateParams();
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.EMITTER_ID, emitterId);
        assertThat(templateParams).containsEntry(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accountName);

        verify(installationEmailProperties, times(1)).getEmail();
    }
}
