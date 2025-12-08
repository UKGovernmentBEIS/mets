package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response;

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
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
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
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountCreatedResponseHandler {

    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final AviationRegistryIntegrationEmailProperties emailProperties;

    public void handleResponse(AccountOpeningEventOutcome accountOpeningEventOutcome , String correlationId) {
        if (IntegrationEventOutcome.ERROR.equals(accountOpeningEventOutcome.getOutcome())) {

            List<IntegrationEventErrorDetails> errors = accountOpeningEventOutcome.getErrors();
            if (!ObjectUtils.isEmpty(errors)) {
                handleErrors(errors, accountOpeningEventOutcome, correlationId);

                log.info(RESPONSE_LOG_FORMAT,
                        NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                        accountOpeningEventOutcome.getEvent().getAccountDetails().getEmitterId(),
                        NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                        "Failed to process an aviation request and notified regulator with errors " + accountOpeningEventOutcome);
            } else {
                log.info(RESPONSE_LOG_FORMAT,
                        NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                        accountOpeningEventOutcome.getEvent().getAccountDetails().getEmitterId(),
                        NotifyRegistryUtils.ACCOUNT_CREATED_INTEGRATION_POINT_KEY,
                        "Failed to process an aviation request, but received unknown error(s) " + accountOpeningEventOutcome);
            }
        }
    }

    private void handleErrors(List<IntegrationEventErrorDetails> errors, AccountOpeningEventOutcome event , String correlationId) {
        Map<String,String> actionErrors = new HashMap<>();
        Map<String,String> infoErrors = new HashMap<>();

        errors.forEach(error -> {
            if (IntegrationEventError.ERROR_0111.equals(error.getError())) {
                actionErrors.put(error.getError().getCode(), error.getError().getMessage());
            } else {
                infoErrors.put(error.getError().getCode(), error.getError().getMessage());
            }
        });

        if(!actionErrors.isEmpty()) {
            notifyRegulator(event, correlationId,
                    actionErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_OPEN_ERROR_TEMPLATE);
        }

        if(!infoErrors.isEmpty()) {
            notifyRegulator(event, correlationId,
                    infoErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_OPEN_ERROR_INFO_TEMPLATE);
        }
    }

    private void notifyRegulator(AccountOpeningEventOutcome event, String correlationId,
                                 Map<String, String> errorsForMail, PmrvNotificationTemplateName templateName) {
        AccountDetailsMessage accountDetails = event.getEvent().getAccountDetails();

        String emitterId = accountDetails.getEmitterId();
        if (emitterId == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "EmitterId not found");
        }

        Account account = accountQueryService.getAccountByEmitterId(emitterId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("Account with emitterId %s not found", emitterId))
                );

        Map<String, Object> templateParams = Map.of(
                PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId(),
                PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail,
                PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId,
                PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, NotifyRegistryUtils.AVIATION_SERVICE_KEY,
                PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName(),
                PmrvEmailNotificationTemplateConstants.PAYLOAD, event

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
