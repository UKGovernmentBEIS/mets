package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.authorization.operator.domain.NewUserActivated;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class OperatorUserNotificationGateway {

    private final RoleService roleService;
    private final AccountQueryService accountQueryService;
    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final UserNotificationService userNotificationService;
    private final NotificationProperties notificationProperties;
    private final JwtProperties jwtProperties;
    private final WebAppProperties webAppProperties;

    /**
     * Sends an {@link PmrvNotificationTemplateName#INVITATION_TO_OPERATOR_ACCOUNT} email with receiver email param as recipient.
     * @param operatorUserInvitationDTO the invited operator user to notify
     * @param accountName the account name that will be used to form the email body
     * @param authorityUuid the uuid that will be used to form the token that will be send with the email body
     */
    public void notifyInvitedUser(OperatorUserInvitationDTO operatorUserInvitationDTO, String accountName,
                                  String authorityUuid) {
        RoleDTO roleDTO = roleService.getRoleByCode(operatorUserInvitationDTO.getRoleCode());
        long expirationInMinutes = jwtProperties.getClaim().getUserInvitationExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
        		PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, roleDTO.getName(),
        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, accountName,
        		PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes,
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()
        ));

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(PmrvNotificationTemplateName.INVITATION_TO_OPERATOR_ACCOUNT)
                        .userEmail(operatorUserInvitationDTO.getEmail())
                        .notificationParams(notificationParams)
                        .linkParamName(PmrvEmailNotificationTemplateConstants.OPERATOR_INVITATION_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.OPERATOR_REGISTRATION_INVITATION_ACCEPTED_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenAction.OPERATOR_INVITATION)
                                .claimValue(authorityUuid)
                                .expirationInterval(expirationInMinutes)
                                .build()
                        )
                        .build()
        );
    }
    
    public void notifyRegisteredUser(OperatorUserDTO operatorUserDTO) {
        EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.USER_ACCOUNT_CREATED.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_EMAIL, operatorUserDTO.getEmail(),
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailInfo, operatorUserDTO.getEmail());
    }

    public void notifyEmailVerification(String email) {
        Map<String, Object> notificationParams = new HashMap<>();
        notificationParams.put(PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR,
                notificationProperties.getEmail().getContactUsLink());

        userNotificationService.notifyUserWithLink(
                UserNotificationWithRedirectionLinkInfo.builder()
                        .templateName(PmrvNotificationTemplateName.EMAIL_CONFIRMATION)
                        .notificationParams(notificationParams)
                        .userEmail(email)
                        .linkParamName(PmrvEmailNotificationTemplateConstants.EMAIL_CONFIRMATION_LINK)
                        .linkPath(NavigationOutcomes.REGISTRATION_EMAIL_VERIFY_CONFIRMATION_URL)
                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                                .jwtTokenAction(JwtTokenAction.USER_REGISTRATION)
                                .claimValue(email)
                                .expirationInterval(jwtProperties.getClaim().getUserInvitationExpIntervalMinutes())
                                .build()
                        )
                        .build()
        );
    }

    public void notifyInviteeAcceptedInvitation(UserInfoDTO inviteeUser) {
        EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.INVITEE_INVITATION_ACCEPTED.getName())
                        .templateParams(Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, "Operator",
                        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                        		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl()))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviteeUser.getEmail());
    }

    public void notifyInviterAcceptedInvitation(UserInfoDTO inviteeUser, UserInfoDTO inviterUser) {
        EmailData<EmailNotificationTemplateData> inviteeInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(PmrvNotificationTemplateName.INVITER_INVITATION_ACCEPTED.getName())
                        .templateParams(Map.of(
                        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_FNAME, inviterUser.getFirstName(),
                        		PmrvEmailNotificationTemplateConstants.USER_ACCOUNT_CREATED_USER_LNAME, inviterUser.getLastName(),
                        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_FNAME, inviteeUser.getFirstName(),
                        		PmrvEmailNotificationTemplateConstants.USER_INVITEE_LNAME, inviteeUser.getLastName()))
                        .build())
                .build();

        notificationEmailService.notifyRecipient(inviteeInfo, inviterUser.getEmail());
    }

    public void notifyUsersUpdateStatus(List<NewUserActivated> activatedOperators) {
        activatedOperators.forEach(user -> {
            try{
                if(AuthorityConstants.EMITTER_CONTACT.equals(user.getRoleCode())){
                    String installationName = accountQueryService.getAccountName(user.getAccountId());
                    userNotificationService.notifyEmitterContactAccountActivation(user.getUserId(), installationName);
                }
                else{
                    userNotificationService.notifyUserAccountActivation(user.getUserId(), "Operator");
                }
            } catch (Exception ex){
                log.error("Exception during sending email for update operator status:", ex);
            }
        });
    }
}
