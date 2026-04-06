package uk.gov.pmrv.api.integration.registry.notification.installation.response;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
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

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.RESPONSE_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationNotificationResponseHandler {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final InstallationRegistryIntegrationEmailProperties emailProperties;

    public void handleResponse(RegulatorNoticeEventOutcome regulatorNoticeEventOutcome, String correlationId){
        if (IntegrationEventOutcome.ERROR.equals(regulatorNoticeEventOutcome.getOutcome())) {

            List<IntegrationEventErrorDetails> errors = regulatorNoticeEventOutcome.getErrors();
            if (!ObjectUtils.isEmpty(errors)) {
                handleErrors(errors, regulatorNoticeEventOutcome, correlationId);

                log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                        regulatorNoticeEventOutcome.getEvent(),
                    NotifyRegistryUtils.ACCOUNT_INSTALLATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                    "Failed to process an installation request and notified regulator with errors " + regulatorNoticeEventOutcome);
            } else {
                log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                        regulatorNoticeEventOutcome.getEvent(),
                    NotifyRegistryUtils.ACCOUNT_INSTALLATION_NOTIFICATION_INTEGRATION_POINT_KEY,
                    "Failed to process an installation request, but received unknown error(s) " + regulatorNoticeEventOutcome);
            }
        }
    }

    private void handleErrors(List<IntegrationEventErrorDetails> errors, RegulatorNoticeEventOutcome regulatorNoticeEventOutcome , String correlationId) {
        Map<String,String> infoErrors = new HashMap<>();
        Map<String,String> actionErrors = new HashMap<>();

        errors.forEach(error -> {
            if (IntegrationEventError.ERROR_0600.equals(error.getError())
                    || IntegrationEventError.ERROR_0601.equals(error.getError())
                    || IntegrationEventError.ERROR_0602.equals(error.getError())
                    || IntegrationEventError.ERROR_0604.equals(error.getError())) {
                infoErrors.put(error.getError().getCode(), error.getError().getMessage());

            } else if (IntegrationEventError.ERROR_0603.equals(error.getError())) {
                actionErrors.put(error.getError().getCode(), error.getError().getMessage());
            }
        });

        if(!infoErrors.isEmpty()) {
            notifyRegulator(regulatorNoticeEventOutcome, correlationId,
                    infoErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_NOTIFICATION_INFO_ERROR_TEMPLATE);
        }
        if(!actionErrors.isEmpty()) {
            notifyRegulator(regulatorNoticeEventOutcome, correlationId, actionErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_NOTIFICATION_ERROR_TEMPLATE);
        }
    }

    private void notifyRegulator(RegulatorNoticeEventOutcome regulatorNoticeEventOutcome, String correlationId,
                                 Map<String, String> errorsForMail, PmrvNotificationTemplateName templateName) {

        String registryId = regulatorNoticeEventOutcome.getEvent().getRegistryId();

        if (registryId == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Registry Id is null");
        }

        InstallationAccount account = installationAccountQueryService.getSingleLiveAccountByRegistryId(Integer.valueOf(registryId));

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail,
                PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId,
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName(),
                PmrvEmailNotificationTemplateConstants.PAYLOAD, regulatorNoticeEventOutcome
        );

        EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(account.getCompetentAuthority())
                        .templateName(templateName.getName())
                        .accountType(account.getAccountType())
                        .templateParams(templateParams)
                        .build())
                .build();
        notificationEmailService.notifyRecipient(emailData, emailProperties.getEmail().get(account.getCompetentAuthority().getCode()));
    }
}
