package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.CONTACT_REGULATOR;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.HOME_URL;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_REJECTED;

/**
 * Handler for the Notify Operator service task of the installation account opening BPMN process.
 */
@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningNotifyOperatorService {

    private final NotificationEmailService notificationEmailService;
    private final UserService userService;
    private final RequestService requestService;
    private final AppProperties appProperties;
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
        ApplicationUserDTO applicantUserDTO = userService.getUserById(userId);

        EmailData emailData = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                .templateName(isApplicationAccepted ? ACCOUNT_APPLICATION_ACCEPTED : ACCOUNT_APPLICATION_REJECTED)
                .templateParams(buildTemplateParams(isApplicationAccepted, accountOpeningDecisionPayload))
                .build())
            .build();
        notificationEmailService.notifyRecipient(emailData, applicantUserDTO.getEmail());
    }

    private Map<String, Object> buildTemplateParams(boolean isApplicationAccepted, AccountOpeningDecisionPayload accountOpeningDecisionPayload) {
        Map<String, Object> dataModelParams = new HashMap<>();
        dataModelParams.put(HOME_URL, appProperties.getWeb().getUrl());
        dataModelParams.put(CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink());

        if (!isApplicationAccepted) {
            dataModelParams.put(ACCOUNT_APPLICATION_REJECTED_REASON, accountOpeningDecisionPayload.getReason());
        }
        return dataModelParams;
    }
}
