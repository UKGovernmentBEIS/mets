package uk.gov.pmrv.api.integration.registry.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistryIdEmailNotifierService {

    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    public void registryIdNonExistenceNotifyRegulator(InstallationAccountDTO accountDTO,String templateName,String sourceSystem) {

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, accountDTO.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, sourceSystem,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accountDTO.getName());

        EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(accountDTO.getCompetentAuthority())
                        .templateName(templateName)
                        .accountType(AccountType.INSTALLATION)
                        .templateParams(templateParams)
                        .build())
                .build();

        String emailAddress = installationEmailProperties.getEmail().get(accountDTO.getCompetentAuthority().getCode());
        notificationEmailService.notifyRecipient(emailData, emailAddress);

    }

}
