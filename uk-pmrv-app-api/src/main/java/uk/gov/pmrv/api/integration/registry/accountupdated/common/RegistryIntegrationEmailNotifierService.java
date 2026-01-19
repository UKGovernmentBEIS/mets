package uk.gov.pmrv.api.integration.registry.accountupdated.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class RegistryIntegrationEmailNotifierService {

    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final InstallationRegistryIntegrationEmailProperties installationEmailProperties;
    private final AviationRegistryIntegrationEmailProperties aviationEmailProperties;


    public void registryIdNonExistenceNotifyRegulatorForAction(AccountDTO accountDTO) {

        AccountType accountType = accountDTO.getAccountType();
        String sourceSystem;
        String emailAddress;
        if(AccountType.INSTALLATION.equals(accountType)) {
            sourceSystem = NotifyRegistryUtils.INSTALLATION_SERVICE_KEY;
            emailAddress = installationEmailProperties.getEmail().get(accountDTO.getCompetentAuthority().getCode());
        }
        else {
            sourceSystem = NotifyRegistryUtils.AVIATION_SERVICE_KEY;
            emailAddress = aviationEmailProperties.getEmail().get(accountDTO.getCompetentAuthority().getCode());
        }

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, accountDTO.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, sourceSystem,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, accountDTO.getName()
        );


        EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(accountDTO.getCompetentAuthority())
                        .templateName(PmrvNotificationTemplateName.REGISTRY_INTEGRATION_ACCOUNT_UPDATE_MISSING_REGISTRY_ID.getName())
                        .accountType(accountType)
                        .templateParams(templateParams)
                        .build())
                .build();
        notificationEmailService.notifyRecipient(emailData, emailAddress);

    }


}
