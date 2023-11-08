package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.config.AppProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class AirRespondToRegulatorCommentsNotificationService {

    private final NotificationEmailService notificationEmailService;
    private final UserAuthService userAuthService;
    private final AuthorityService authorityService;
    private final AccountCaSiteContactService accountCaSiteContactService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final AppProperties appProperties;

    public void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(final Request request) {
        this.sendNotification(request, NotificationTemplateName.AIR_NOTIFICATION_OPERATOR_RESPONSE);
    }

    public void sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(final Request request) {
        this.sendNotification(request, NotificationTemplateName.AIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE);
    }

    private void sendNotification(final Request request, NotificationTemplateName templateName) {
        
        final Set<String> recipientsEmails = new HashSet<>();
        final String reviewer = request.getPayload().getRegulatorReviewer();
        final Long accountId = request.getAccountId();

        // Find Regulator reviewer
        Optional.ofNullable(authorityService.findStatusByUsers(List.of(reviewer)).get(reviewer)).ifPresent(reviewerStatus -> {
            if(AuthorityStatus.ACTIVE.equals(reviewerStatus)) {
                UserInfoDTO userReviewer = userAuthService.getUserByUserId(reviewer);
                recipientsEmails.add(userReviewer.getEmail());
            }
        });

        // Find Site Contact
        accountCaSiteContactService.findCASiteContactByAccount(accountId).ifPresent(siteContact -> {
            UserInfoDTO userSiteContact = userAuthService.getUserByUserId(siteContact);
            recipientsEmails.add(userSiteContact.getEmail());
        });

        // Send the emails
        if(!recipientsEmails.isEmpty()) {
            InstallationOperatorDetails operatorDetails = installationOperatorDetailsQueryService
                    .getInstallationOperatorDetails(accountId);

            EmailData notifyInfo = EmailData.builder()
                    .notificationTemplateData(EmailNotificationTemplateData.builder()
                            .templateName(templateName)
                            .templateParams(Map.of(
                                    EmailNotificationTemplateConstants.ACCOUNT_NAME, operatorDetails.getInstallationName(),
                                    EmailNotificationTemplateConstants.EMITTER_ID, operatorDetails.getEmitterId(),
                                    EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl()
                            ))
                            .build())
                    .build();

            notificationEmailService.notifyRecipients(notifyInfo, new ArrayList<>(recipientsEmails), List.of());
        }
    }
}
