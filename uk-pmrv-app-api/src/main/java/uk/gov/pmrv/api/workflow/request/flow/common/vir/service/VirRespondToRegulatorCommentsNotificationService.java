package uk.gov.pmrv.api.workflow.request.flow.common.vir.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
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
public class VirRespondToRegulatorCommentsNotificationService {

    private final NotificationEmailService notificationEmailService;
    private final UserAuthService userAuthService;
    private final AuthorityService authorityService;
    private final AccountCaSiteContactService accountCaSiteContactService;
    private final AccountQueryService accountQueryService;
    private final AppProperties appProperties;

    public void sendSubmittedResponseToRegulatorCommentsNotificationToRegulator(final Request request) {
        sendNotification(request, NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_RESPONSE);
    }

    public void sendDeadlineResponseToRegulatorCommentsNotificationToRegulator(final Request request) {
        sendNotification(request, NotificationTemplateName.VIR_NOTIFICATION_OPERATOR_MISSES_DEADLINE);
    }

    private void sendNotification(final Request request, NotificationTemplateName templateName) {
        Set<String> recipientsEmails = new HashSet<>();
        String reviewer = request.getPayload().getRegulatorReviewer();
        Long accountId = request.getAccountId();

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
            AccountInfoDTO accountInfoDTO = accountQueryService.getAccountInfoDTOById(accountId);

            EmailData notifyInfo = EmailData.builder()
                    .notificationTemplateData(EmailNotificationTemplateData.builder()
                            .templateName(templateName)
                            .templateParams(Map.of(
                                    EmailNotificationTemplateConstants.ACCOUNT_NAME, accountInfoDTO.getName(),
                                    EmailNotificationTemplateConstants.EMITTER_ID, accountInfoDTO.getEmitterId(),
                                    EmailNotificationTemplateConstants.HOME_URL, appProperties.getWeb().getUrl()
                            ))
                            .build())
                    .build();

            notificationEmailService.notifyRecipients(notifyInfo, new ArrayList<>(recipientsEmails), List.of());
        }
    }
}
