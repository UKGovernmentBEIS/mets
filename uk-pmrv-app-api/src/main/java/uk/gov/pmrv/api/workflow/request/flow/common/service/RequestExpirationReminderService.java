package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestExpirationReminderService {

    private final RequestService requestService;
    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<PmrvEmailNotificationTemplateData> notificationEmailService;
    private final CompetentAuthorityDTOByRequestResolverDelegator competentAuthorityDTOByRequestResolverDelegator;

    public void sendExpirationReminderNotification(String requestId, NotificationTemplateExpirationReminderParams expirationParams) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final AccountInfoDTO accountInfo = accountQueryService.getAccountInfoDTOById(accountId);
        final AccountType accountType = request.getType().getAccountType();
        
        final Map<String, Object> templateParams = new HashMap<>();
        templateParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, accountInfo.getName());
        templateParams.put(PmrvEmailNotificationTemplateConstants.EMITTER_ID, accountInfo.getEmitterId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_ID, request.getId());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        templateParams.put(PmrvEmailNotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        
        final CompetentAuthorityDTO competentAuthorityDTO = competentAuthorityDTOByRequestResolverDelegator.resolveCA(request, accountType);

        templateParams.put(PmrvEmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, competentAuthorityDTO.getEmail());
        
        final EmailData<PmrvEmailNotificationTemplateData> emailData = EmailData.<PmrvEmailNotificationTemplateData>builder()
                .notificationTemplateData(PmrvEmailNotificationTemplateData.builder()
                        .competentAuthority(request.getCompetentAuthority())
                        .templateName(PmrvNotificationTemplateName.GENERIC_EXPIRATION_REMINDER.getName())
                        .accountType(request.getType().getAccountType())
                        .templateParams(templateParams)
                        .build())
                .build();
        
        notificationEmailService.notifyRecipient(emailData, expirationParams.getRecipient().getEmail());
    }
}
