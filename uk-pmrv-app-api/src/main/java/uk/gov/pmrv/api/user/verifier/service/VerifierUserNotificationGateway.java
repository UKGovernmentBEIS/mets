package uk.gov.pmrv.api.user.verifier.service;

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
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierUserNotificationGateway {

    private final UserNotificationService userNotificationService;
    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
    private final WebAppProperties webAppProperties;

    /**
     * Notifies by email the user that an invitation to participate in a verification body is awaiting to be confirmed.
     * @param verifierUserInvitation the {@link VerifierUserInvitationDTO} containing the invited user's info
     * @param authorityUuid the authority uuid
     */
    public void notifyInvitedUser(VerifierUserInvitationDTO verifierUserInvitation, String authorityUuid) {
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
        		PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, verifierUserInvitation.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.APPLICANT_LNAME, verifierUserInvitation.getLastName(),
        		PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes,
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()
        ));

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(PmrvNotificationTemplateName.INVITATION_TO_VERIFIER_ACCOUNT)
                        .userEmail(verifierUserInvitation.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(PmrvEmailNotificationTemplateConstants.VERIFIER_INVITATION_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.VERIFIER_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenAction.VERIFIER_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(UserInfoDTO invitee) {
        EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.INVITEE_INVITATION_ACCEPTED.getName())
                        .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, "Verifier",
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                        		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl()))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(inviteeInfo, invitee.getEmail());
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO invitee, UserInfoDTO inviter) {
        EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
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

    public void notifyUsersUpdateStatus(List<String> activatedVerifiers) {
        activatedVerifiers.forEach(user -> {
            try{
                userNotificationService.notifyUserAccountActivation(user, "Verifier");
            } catch (Exception ex){
                log.error("Exception during sending email for update verifier status:", ex);
            }
        });
    }
}
