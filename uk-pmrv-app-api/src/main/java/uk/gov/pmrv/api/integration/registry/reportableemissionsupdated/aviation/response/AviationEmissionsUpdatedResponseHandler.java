package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseErrorCode;
import uk.gov.pmrv.api.integration.registry.common.RegistryResponseStatus;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.RESPONSE_LOG_FORMAT;
import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.capitalizeFirstLetter;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class AviationEmissionsUpdatedResponseHandler {
    private static final String SERVICE_KEY = "aviation";
    private static final String INTEGRATION_POINT_KEY = "reportable-emissions-updated";

    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final AviationRegistryIntegrationEmailProperties emailProperties;

    public void handleResponse(AccountEmissionsUpdatedResponseEvent event, String correlationId) {
        if (event.getOutcome() == RegistryResponseStatus.ERROR) {
            Map<String, String> errorsForMail = event.getErrors().stream()
                .filter(err -> EnumUtils.isValidEnum(RegistryResponseErrorCode.class, err))
                .map(RegistryResponseErrorCode::valueOf)
                .collect(Collectors.toMap(RegistryResponseErrorCode::getCode, RegistryResponseErrorCode::getDescription));
            if (!ObjectUtils.isEmpty(errorsForMail)) {
                if (errorsForMail.containsKey(RegistryResponseErrorCode.ERROR_0803.getCode())) {
                    notifyRegulator(event, correlationId,
                            Map.of(RegistryResponseErrorCode.ERROR_0803.getCode(),
                                    RegistryResponseErrorCode.ERROR_0803.getDescription()),
                        PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ERROR_ACTION_TEMPLATE);
                }

                Map<String, String> remainingErrors = errorsForMail.entrySet()
                        .stream()
                        .filter(entry -> !entry.getKey().equals(RegistryResponseErrorCode.ERROR_0803.getCode()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                if (!ObjectUtils.isEmpty(remainingErrors)) {
                    notifyRegulator(event, correlationId, remainingErrors,
                            PmrvNotificationTemplateName.REGISTRY_INTEGRATION_RESPONSE_ERROR_INFO_TEMPLATE);
                }

                log.info(RESPONSE_LOG_FORMAT,
                    SERVICE_KEY,
                    event.getEvent().getRegistryId(),
                    INTEGRATION_POINT_KEY,
                    "Failed to process an aviation request and notified regulator with errors " + event);
            } else {
                log.info(RESPONSE_LOG_FORMAT,
                    SERVICE_KEY,
                    event.getEvent().getRegistryId(),
                    INTEGRATION_POINT_KEY,
                    "Failed to process an aviation request, but received unknown error(s) " + event);
            }
        }
    }

    private void notifyRegulator(AccountEmissionsUpdatedResponseEvent event, String correlationId,
                                 Map<String, String> errorsForMail, PmrvNotificationTemplateName templateName) {
        AccountEmissionsUpdatedRequestEvent initialEvent = event.getEvent();
        Account account = accountQueryService.findAccountByRegistryId(initialEvent.getRegistryId());
        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, account.getEmitterId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.REGISTRY_ID, initialEvent.getRegistryId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail);
        templateParams.put(PmrvEmailNotificationTemplateConstants.YEAR, initialEvent.getReportingYear().getValue());
        templateParams.put(PmrvEmailNotificationTemplateConstants.EMISSIONS, initialEvent.getReportableEmissions());
        templateParams.put(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, correlationId);
        templateParams.put(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, capitalizeFirstLetter(SERVICE_KEY));
        templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, account.getName());

        final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
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
