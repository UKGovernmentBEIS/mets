package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response.AviationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response.InstallationRegistryIntegrationEmailProperties;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils.REQUEST_LOG_FORMAT;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class OperatorIdErrorNotifierService {

    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final AviationRegistryIntegrationEmailProperties aviationEmailProperties;
    private final InstallationRegistryIntegrationEmailProperties installationEmailProperties;

    @Value("${registry.integration.error.handle.fordway}")
    private String errorHandleFordwayEmail;


    public void notifyAuthority(NotifyErrorDTO notifyErrorDTO) {
        Map<String,String> errorsForMail = notifyErrorDTO.getOutcome().getErrors().stream()
                .map(IntegrationEventErrorDetails::getError)
                .collect(Collectors.toMap(IntegrationEventError::getCode, IntegrationEventError::getMessage));
        if(errorsForMail.containsKey("0200") || errorsForMail.containsKey("0201")) {
            notifyFordway(notifyErrorDTO, errorsForMail);
            notifyRegulator(notifyErrorDTO,errorsForMail,true);
        } else if (errorsForMail.containsKey("0202")) {
            notifyRegulator(notifyErrorDTO,errorsForMail,true);
        } else if (errorsForMail.containsKey("0203") || errorsForMail.containsKey("0204") ||
                errorsForMail.containsKey("0205")) {
            notifyRegulator(notifyErrorDTO, errorsForMail,false);
        } else {
            log.error(REQUEST_LOG_FORMAT, notifyErrorDTO.getService(), notifyErrorDTO.getEvent().getEmitterId(),
                    NotifyRegistryUtils.OPERATOR_ID_INTEGRATION_POINT_KEY, "Unable to notify authorities with unknown errors" + errorsForMail);
        }
    }

    private void notifyFordway(NotifyErrorDTO notifyErrorDTO,Map<String,String> errorsForMail) {

        notificationEmailService.notifyRecipient(buildEmailData(notifyErrorDTO,errorsForMail,
                PmrvNotificationTemplateName.REGISTRY_INTEGRATION_OPERATOR_ID_FORDWAY_ACTION_TEMPLATE,true), errorHandleFordwayEmail);
    }

    private void notifyRegulator(NotifyErrorDTO notifyErrorDTO,Map<String,String> errorsForMail, boolean isInfo) {
        if(ObjectUtils.isEmpty(notifyErrorDTO.getAuthority())) {
            return;
        }

        PmrvNotificationTemplateName templateName = isInfo ? PmrvNotificationTemplateName.REGISTRY_INTEGRATION_OPERATOR_ID_REGULATOR_INFO_TEMPLATE :
                PmrvNotificationTemplateName.REGISTRY_INTEGRATION_OPERATOR_ID_REGULATOR_ACTION_TEMPLATE;
        String email = notifyErrorDTO.getService().equals("Installation") ?
                installationEmailProperties.getEmail().get(notifyErrorDTO.getAuthority().getCode()) :
                aviationEmailProperties.getEmail().get(notifyErrorDTO.getAuthority().getCode());
        notificationEmailService.notifyRecipient(buildEmailData(notifyErrorDTO,errorsForMail,templateName,false), email);
    }

    private EmailData<PmrvEmailNotificationTemplateData> buildEmailData(NotifyErrorDTO notifyErrorDTO,
                                                                        Map<String,String> errorsForMail,
                                                                        PmrvNotificationTemplateName templateName,boolean isFordway) {
        CompetentAuthorityEnum competentAuthority = notifyErrorDTO.getAuthority();

        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, notifyErrorDTO.getEvent().getEmitterId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.ERRORS, errorsForMail);
        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME,notifyErrorDTO.getEvent().getRegulator());
        templateParams.put(PmrvEmailNotificationTemplateConstants.CORRELATION_ID, notifyErrorDTO.getCorrelationId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.SOURCE_SYSTEM, notifyErrorDTO.getService());
        templateParams.put(PmrvEmailNotificationTemplateConstants.OPERATOR_NAME, notifyErrorDTO.getAccountName());
        templateParams.put(PmrvEmailNotificationTemplateConstants.REGISTRY_ID,notifyErrorDTO.getEvent().getOperatorId());


        // We override the regulator with england in case the regulator value is missing
        // because for fordway all the templates are the same for all regulators
        if (isFordway && ObjectUtils.isEmpty(competentAuthority)) {
            competentAuthority = CompetentAuthorityEnum.ENGLAND;
        }
        return EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(competentAuthority)
                        .templateName(templateName.getName())
                        .accountType(notifyErrorDTO.getService().equals("Installation") ? AccountType.INSTALLATION : AccountType.AVIATION)
                        .templateParams(templateParams)
                        .build())
                .build();
    }


}
