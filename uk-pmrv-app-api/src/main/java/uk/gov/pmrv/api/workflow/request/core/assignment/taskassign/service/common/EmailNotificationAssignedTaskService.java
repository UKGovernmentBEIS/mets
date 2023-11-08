package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common;

import static java.util.Map.entry;

import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Log4j2
@Service
@AllArgsConstructor
public class EmailNotificationAssignedTaskService {

    private final NotificationEmailService notificationEmailService;
    private final UserAuthService userAuthService;
    private final AppProperties appProperties;

    /**
     * Sends an email notification to the specified recipient.
     * This method retrieves the user's information based on the provided {@code userId}, constructs the email template
     * data, and sends the email to the recipient using the {@link NotificationEmailService}.
     *
     * @param userId the unique identifier of the recipient user. {@link String}.
     */
    public void sendEmailToRecipient(String userId) {
        if (userId == null) {
            log.error("The userId cannot be null.");
            return;
        }
        UserInfoDTO userInfoDTO = userAuthService.getUserByUserId(userId);

        notificationEmailService.notifyRecipient(
            EmailData.builder()
                .notificationTemplateData(constructEmailTemplateData(appProperties.getWeb().getUrl()))
                .attachments(Collections.emptyMap())
                .build(),
            userInfoDTO.getEmail()
        );
    }

    private EmailNotificationTemplateData constructEmailTemplateData(String homePage) {
        return EmailNotificationTemplateData.builder()
            .templateName(NotificationTemplateName.EMAIL_ASSIGNED_TASK)
            .templateParams(
                Map.ofEntries(
                    entry(EmailNotificationTemplateConstants.HOME_URL, homePage)))
            .build();
    }
}
