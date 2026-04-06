package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.RESPONSE_LOG_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class AccountContactResponseHandler {

    private final AccountQueryService accountQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final InstallationRegistryIntegrationEmailProperties installationEmailProperties;
    private final AviationRegistryIntegrationEmailProperties aviationEmailProperties;

    private static final Set<IntegrationEventError> INFO_ERRORS = Set.of(IntegrationEventError.ERROR_0700,IntegrationEventError.ERROR_0701,IntegrationEventError.ERROR_0702);
    private static final Set<IntegrationEventError> ACTION_ERRORS = Set.of(IntegrationEventError.ERROR_0703);


    public void handleResponse(MetsContactsEventOutcome eventOutcome,String correlationId,String systemIdentifier) {
        if(IntegrationEventOutcome.ERROR.equals(eventOutcome.getOutcome())) {
            handleErrors(eventOutcome,correlationId,systemIdentifier);
        }
    }

    private void handleErrors(MetsContactsEventOutcome eventOutcome,String correlationId,String systemIdentifier) {
        List<IntegrationEventErrorDetails> errors = eventOutcome.getErrors();
        if (ObjectUtils.isEmpty(errors)) {
            log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY.equals(systemIdentifier) ? NotifyRegistryUtils.INSTALLATION_SERVICE_KEY :
                            NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                    eventOutcome.getAccountIdentifier(),
                    NotifyRegistryUtils.ACCOUNT_CONTACT_INTEGRATION_POINT_KEY,
                    "Tried to process a contacts response event error, but received unknown error(s) " + eventOutcome);
        }

        Map<String,String> infoErrors = new HashMap<>();
        Map<String,String> actionErrors = new HashMap<>();

        for (IntegrationEventErrorDetails errorDetails : errors) {
            if(INFO_ERRORS.contains(errorDetails.getError())) {
                infoErrors.put(errorDetails.getError().getCode(), errorDetails.getError().getMessage());
            }
            else if(ACTION_ERRORS.contains(errorDetails.getError())) {
                actionErrors.put(errorDetails.getError().getCode(), errorDetails.getError().getMessage());
            }
        }

        log.info(RESPONSE_LOG_FORMAT,
                NotifyRegistryUtils.INSTALLATION_SERVICE_KEY.equals(systemIdentifier) ? NotifyRegistryUtils.INSTALLATION_SERVICE_KEY :
                        NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                eventOutcome.getAccountIdentifier(),
                NotifyRegistryUtils.ACCOUNT_CONTACT_INTEGRATION_POINT_KEY,
                "Processed error response from registry for the integration point contacts " + eventOutcome);

        notifyRegulator(eventOutcome,correlationId,systemIdentifier,infoErrors, PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_INFO_TEMPLATE);
        notifyRegulator(eventOutcome,correlationId,systemIdentifier,actionErrors,PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_CONTACT_ERROR_ACTION_TEMPLATE);
    }

    private void notifyRegulator(MetsContactsEventOutcome eventOutcome, String correlationId, String systemIdentifier,
                                 Map<String,String> errorsForMail, PmrvNotificationTemplateName templateName) {
        if(errorsForMail.isEmpty()) return;

        String registryId = eventOutcome.getAccountIdentifier();
        EmailData<PmrvEmailNotificationTemplateData> emailData = null;
        String recipient = null;

       if (NotifyRegistryUtils.INSTALLATION_SERVICE_KEY.equals(systemIdentifier)) {
           InstallationAccount installationAccount = installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(registryId));

            emailData = getEmailData(errorsForMail, correlationId, systemIdentifier, eventOutcome, templateName, installationAccount);

            recipient = installationEmailProperties.getEmail().get(installationAccount.getCompetentAuthority().getCode());

        } else if (NotifyRegistryUtils.AVIATION_SERVICE_KEY.equals(systemIdentifier)) {
            Account account = accountQueryService.getAccountByRegistryId(Integer.valueOf(eventOutcome.getAccountIdentifier()))
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

            emailData = getEmailData(errorsForMail, correlationId, systemIdentifier, eventOutcome, templateName, account);

            recipient = aviationEmailProperties.getEmail().get(account.getCompetentAuthority().getCode());
        }

        notificationEmailService.notifyRecipient(emailData, recipient);
    }

    private EmailData<PmrvEmailNotificationTemplateData> getEmailData(Map<String,String> errorsForMail, String correlationId,  String systemIdentifier,
                                                                      MetsContactsEventOutcome eventOutcome, PmrvNotificationTemplateName templateName,
                                                                      Account account) {
        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail,
                PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId,
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, systemIdentifier,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName(),
                PmrvEmailNotificationTemplateConstants.PAYLOAD, eventOutcome);

        return EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(account.getCompetentAuthority())
                        .templateName(templateName.getName())
                        .accountType(account.getAccountType())
                        .templateParams(templateParams)
                        .build())
                .build();
    }
}
