package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response;

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
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
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
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptResponseHandler {

    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final AviationRegistryIntegrationEmailProperties emailProperties;

    public void handleResponse(AccountExemptionUpdateEventOutcome eventOutcome , String correlationId) {
        if (IntegrationEventOutcome.ERROR.equals(eventOutcome.getOutcome())) {

            List<IntegrationEventErrorDetails> errors = eventOutcome.getErrors();
            if (!ObjectUtils.isEmpty(errors)) {
                handleErrors(errors, eventOutcome, correlationId);

                log.info(RESPONSE_LOG_FORMAT,
                        NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                        eventOutcome.getEvent(),
                        NotifyRegistryUtils.ACCOUNT_AVIATION_EXEMPT_UPDATE_INTEGRATION_POINT_KEY,
                        "Failed to process an aviation request and notified regulator with errors " + eventOutcome);
            } else {
                log.info(RESPONSE_LOG_FORMAT,
                        NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                        eventOutcome.getEvent(),
                        NotifyRegistryUtils.ACCOUNT_AVIATION_EXEMPT_UPDATE_INTEGRATION_POINT_KEY,
                        "Failed to process an aviation request, but received unknown error(s) " + eventOutcome);
            }
        }
    }

    private void handleErrors(List<IntegrationEventErrorDetails> errors, AccountExemptionUpdateEventOutcome eventOutcome , String correlationId) {
        Map<String,String> infoErrors = new HashMap<>();
        Map<String,String> actionErrors = new HashMap<>();

        errors.forEach(error -> {
            if (IntegrationEventError.ERROR_0400.equals(error.getError())
                    || IntegrationEventError.ERROR_0401.equals(error.getError())
                    || IntegrationEventError.ERROR_0402.equals(error.getError())) {
                infoErrors.put(error.getError().getCode(), error.getError().getMessage());

            } else  if (IntegrationEventError.ERROR_0403.equals(error.getError())
                    || IntegrationEventError.ERROR_0404.equals(error.getError())
                    || IntegrationEventError.ERROR_0405.equals(error.getError())
                    || IntegrationEventError.ERROR_0406.equals(error.getError())
                    || IntegrationEventError.ERROR_0407.equals(error.getError())) {
                actionErrors.put(error.getError().getCode(), error.getError().getMessage());
            }
        });

        if(!infoErrors.isEmpty()) {
            notifyRegulator(eventOutcome, correlationId,
                    infoErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_EXEMPT_FLAG_ERROR_INFO_TEMPLATE);
        }
        if(!actionErrors.isEmpty()) {
            notifyRegulator(eventOutcome, correlationId, actionErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_EXEMPT_FLAG_ERROR_ACTION_TEMPLATE);
        }
    }

    private void notifyRegulator(AccountExemptionUpdateEventOutcome eventOutcome, String correlationId,
                                 Map<String, String> errorsForMail, PmrvNotificationTemplateName templateName) {

        Long registryId = eventOutcome.getEvent().getRegistryId();
        if (registryId == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "RegistryId not found");
        }

        Account account = accountQueryService.findAccountByRegistryId(Math.toIntExact(registryId));
        if (account == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found");
        }

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail,
                PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId,
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName(),
                PmrvEmailNotificationTemplateConstants.PAYLOAD, eventOutcome
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
