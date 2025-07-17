package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.application.UserServiceDelegator;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for the Notify Operator service task of the installation account opening BPMN process.
 */
@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningNotifyOperatorService {

    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final UserServiceDelegator userServiceDelegator;
    private final RequestService requestService;
    private final WebAppProperties webAppProperties;
    private final NotificationProperties notificationProperties;

    public void execute(String requestId) {
        Request request = requestService.findRequestById(requestId);
        sendAccountApplicationUpdateEmail(request);
    }

    private void sendAccountApplicationUpdateEmail(Request request) {
        AccountOpeningDecisionPayload accountOpeningDecisionPayload =
            ((InstallationAccountOpeningRequestPayload) request.getPayload()).getAccountOpeningDecisionPayload();
        boolean isApplicationAccepted = accountOpeningDecisionPayload.getDecision() == Decision.ACCEPTED;
        final String userId = request.getPayload().getOperatorAssignee();
        final UserDTO userDTO = userServiceDelegator.getUserById(userId);

        EmailData<EmailNotificationTemplateData> emailData = EmailData.<EmailNotificationTemplateData>builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
						.templateName(isApplicationAccepted
								? PmrvNotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED.getName()
								: PmrvNotificationTemplateName.ACCOUNT_APPLICATION_REJECTED.getName())
                .templateParams(buildTemplateParams(isApplicationAccepted, accountOpeningDecisionPayload))
                .build())
            .build();
        notificationEmailService.notifyRecipient(emailData, userDTO.getEmail());
    }

    private Map<String, Object> buildTemplateParams(boolean isApplicationAccepted, AccountOpeningDecisionPayload accountOpeningDecisionPayload) {
        Map<String, Object> dataModelParams = new HashMap<>();
        dataModelParams.put(PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl());
        dataModelParams.put(PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink());

        if (!isApplicationAccepted) {
            dataModelParams.put(PmrvEmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON, accountOpeningDecisionPayload.getReason());
        }
        return dataModelParams;
    }
}
