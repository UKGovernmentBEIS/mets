package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class RegulatorUserNotificationGateway {

    private final UserNotificationService userNotificationService;
    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
    private final WebAppProperties webAppProperties;

    public void notifyInvitedUser(RegulatorInvitedUserDetailsDTO invitedUserDetails, String authorityUuid) {
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
        		PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, invitedUserDetails.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.APPLICANT_LNAME, invitedUserDetails.getLastName(),
        		PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes,
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink())
        );

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(PmrvNotificationTemplateName.INVITATION_TO_REGULATOR_ACCOUNT)
                        .userEmail(invitedUserDetails.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(PmrvEmailNotificationTemplateConstants.REGULATOR_INVITATION_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.REGULATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenAction.REGULATOR_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(UserInfoDTO invitee) {
        final EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(PmrvNotificationTemplateName.INVITEE_INVITATION_ACCEPTED.getName())
                    .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, "Regulator",
                    		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                    		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl()))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, invitee.getEmail());
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO invitee, UserInfoDTO inviter) {
        final EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(PmrvNotificationTemplateName.INVITER_INVITATION_ACCEPTED.getName())
                    .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviter.getFirstName(),
                    		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviter.getLastName(),
                    		PmrvEmailNotificationTemplateConstants.USER_INVITEE_FNAME, invitee.getFirstName(),
                    		PmrvEmailNotificationTemplateConstants.USER_INVITEE_LNAME, invitee.getLastName()))
                    .build())
            .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviter.getEmail());
    }

    public void sendUpdateNotifications(final List<String> activatedRegulators) {

        // send notifications for accounts that have been activated
        activatedRegulators
            .forEach(userId -> {
                try {
                    userNotificationService.notifyUserAccountActivation(userId, "Regulator");
                } catch (Exception ex) {
                    log.error("Exception during sending email for regulator activation:", ex);
                }
            });
    }
}
