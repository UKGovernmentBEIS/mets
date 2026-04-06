package uk.gov.pmrv.api.integration.registry.withholdflag.installation.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.RESPONSE_LOG_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagResponseHandler {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final InstallationRegistryIntegrationEmailProperties installationEmailProperties;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;


    private static final Set<IntegrationEventError> INFO_ERRORS = Set.of(IntegrationEventError.ERROR_0500,
            IntegrationEventError.ERROR_0501,IntegrationEventError.ERROR_0502,IntegrationEventError.ERROR_0508);
    private static final Set<IntegrationEventError> ACTION_ERRORS = Set.of(IntegrationEventError.ERROR_0503,
            IntegrationEventError.ERROR_0504, IntegrationEventError.ERROR_0505,IntegrationEventError.ERROR_0506);

    public void handleResponse(AccountWithholdUpdateEventOutcome eventOutcome, String correlationId) {
        if(IntegrationEventOutcome.ERROR.equals(eventOutcome.getOutcome())) {
            handleErrors(eventOutcome,correlationId);
        }
    }

    private void handleErrors(AccountWithholdUpdateEventOutcome eventOutcome, String correlationId) {
        List<IntegrationEventErrorDetails> errors = eventOutcome.getErrors();
        if (ObjectUtils.isEmpty(errors)) {
            log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                    eventOutcome.getEvent().getRegistryId(),
                    NotifyRegistryUtils.ACCOUNT_INSTALLATION_WITHHOLD_FLAG_INTEGRATION_POINT_KEY,
                    "Tried to process a withhold flag response event error, but received unknown error(s) " + eventOutcome);
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
                NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                eventOutcome.getEvent().getRegistryId(),
                NotifyRegistryUtils.ACCOUNT_INSTALLATION_WITHHOLD_FLAG_INTEGRATION_POINT_KEY,
                "Processed error response from registry for the integration point withhold flag " + eventOutcome);

        notifyRegulator(eventOutcome,correlationId,infoErrors, PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_INFO_TEMPLATE);
        notifyRegulator(eventOutcome,correlationId,actionErrors, PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_WITHHOLD_FLAG_ERROR_ACTION_TEMPLATE);

    }

    private void notifyRegulator(AccountWithholdUpdateEventOutcome eventOutcome, String correlationId, Map<String,String> errorsForMail, PmrvNotificationTemplateName templateName) {
        if(errorsForMail.isEmpty()) return;

        Long registryId = eventOutcome.getEvent().getRegistryId();

        if (registryId == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Registry Id is null");
        }

        InstallationAccount account = installationAccountQueryService.getSingleLiveAccountByRegistryId(Math.toIntExact(registryId));

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail,
                PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId,
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName(),
                PmrvEmailNotificationTemplateConstants.PAYLOAD, eventOutcome);

        EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(account.getCompetentAuthority())
                        .templateName(templateName.getName())
                        .accountType(account.getAccountType())
                        .templateParams(templateParams)
                        .build())
                .build();

        String recipient = installationEmailProperties.getEmail().get(account.getCompetentAuthority().getCode());

        notificationEmailService.notifyRecipient(emailData, recipient);
    }

}
