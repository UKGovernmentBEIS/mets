package uk.gov.pmrv.api.integration.registry.accountupdated.installation.response;

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
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.RESPONSE_LOG_FORMAT;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountUpdatedResponseHandler {

    private final InstallationAccountQueryService installationAccountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final InstallationRegistryIntegrationEmailProperties emailProperties;

    public void handleResponse(AccountUpdatingEventOutcome accountUpdatingEventOutcome, String correlationId){
        if (IntegrationEventOutcome.ERROR.equals(accountUpdatingEventOutcome.getOutcome())) {

            List<IntegrationEventErrorDetails> errors = accountUpdatingEventOutcome.getErrors();
            if (!ObjectUtils.isEmpty(errors)) {
                handleErrors(errors, accountUpdatingEventOutcome, correlationId);

                log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                        accountUpdatingEventOutcome.getEvent().getAccountDetails(),
                    NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY,
                    "Failed to process an installation request and notified regulator with errors " + accountUpdatingEventOutcome);
            } else {
                log.info(RESPONSE_LOG_FORMAT,
                    NotifyRegistryUtils.INSTALLATION_SERVICE_KEY,
                        accountUpdatingEventOutcome.getEvent().getAccountDetails(),
                    NotifyRegistryUtils.ACCOUNT_UPDATED_INTEGRATION_POINT_KEY,
                    "Failed to process an installation request, but received unknown error(s) " + accountUpdatingEventOutcome);
            }
        }
    }

    private void handleErrors(List<IntegrationEventErrorDetails> errors, AccountUpdatingEventOutcome event , String correlationId) {
        Map<String,String> infoErrors = new HashMap<>();
        Map<String,String> actionErrors = new HashMap<>();

        Set<String> INFO_ERROR_CODES = Set.of("0301", "0303", "0304", "0307", "0308", "0309", "0312", "0316", "0318", "0319");
        Set<String> ACTION_ERROR_CODES = Set.of("0311", "0313", "0314", "0315", "0317");

        for (IntegrationEventErrorDetails error : errors) {
            String code = error.getError().getCode();
            String message = error.getError().getMessage();

            if (INFO_ERROR_CODES.contains(code)) {
                infoErrors.put(code, message);
            } else if (ACTION_ERROR_CODES.contains(code)) {
                actionErrors.put(code, message);
            }
        }
        if(!actionErrors.isEmpty()) {
            notifyRegulator(event, correlationId, actionErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_UPDATE_ERROR_TEMPLATE);
        }
        if(!infoErrors.isEmpty()) {
            notifyRegulator(event, correlationId,
                    infoErrors,
                    PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ACCOUNT_UPDATE_INFO_ERROR_TEMPLATE);
        }
    }

    private void notifyRegulator(AccountUpdatingEventOutcome event, String correlationId,
                                 Map<String, String> errorsForMail, PmrvNotificationTemplateName templateName) {
        UpdateAccountDetailsMessage accountDetails = event.getEvent().getAccountDetails();
        String registryId = accountDetails.getRegistryId();

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
