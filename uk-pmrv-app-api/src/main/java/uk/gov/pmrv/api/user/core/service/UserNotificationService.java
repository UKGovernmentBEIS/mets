package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.netz.api.common.config.WebAppProperties;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailData;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.NotificationEmailService;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationParams;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserAuthService userAuthService;
    private final NotificationEmailService<EmailNotificationTemplateData> notificationEmailService;
    private final WebAppProperties webAppProperties;
    private final NotificationProperties notificationProperties;
    private final JwtTokenService jwtTokenService;

    /**
     * Sends email notification containing a redirection link to user.
     * @param notificationInfo {@link UserNotificationWithRedirectionLinkInfo}
     */
    public void notifyUserWithLink(UserNotificationWithRedirectionLinkInfo notificationInfo) {
        String redirectionLink = constructRedirectionLink(notificationInfo.getLinkPath(), notificationInfo.getTokenParams());

        Map<String, Object> notificationParameters = !ObjectUtils.isEmpty(notificationInfo.getNotificationParams()) ?
            notificationInfo.getNotificationParams() :
            new HashMap<>();

        notificationParameters.put(notificationInfo.getLinkParamName(), redirectionLink);

        notifyUser(notificationInfo.getUserEmail(), notificationInfo.getTemplateName(), notificationParameters);
    }

    public void notifyUserAccountActivation(String userId, String roleName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), PmrvNotificationTemplateName.USER_ACCOUNT_ACTIVATION, Map.of(PmrvEmailNotificationTemplateConstants.USER_ROLE_TYPE, roleName,
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
        		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl()));
    }

    public void notifyEmitterContactAccountActivation(String userId, String installationName) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), PmrvNotificationTemplateName.INVITATION_TO_EMITTER_CONTACT, Map.of(PmrvEmailNotificationTemplateConstants.APPLICANT_FNAME, user.getFirstName(),
        		PmrvEmailNotificationTemplateConstants.APPLICANT_LNAME, user.getLastName(),
        		PmrvEmailNotificationTemplateConstants.ACCOUNT_NAME, installationName,
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()));
    }
    
    public void notifyUserPasswordReset(String userId) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), PmrvNotificationTemplateName.RESET_PASSWORD_CONFIRMATION, Map.of(
        		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl(),
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()));
    }
    
    public void notifyUserReset2Fa(String userId) {
        UserInfoDTO user = userAuthService.getUserByUserId(userId);
        
        notifyUser(user.getEmail(), PmrvNotificationTemplateName.RESET_2FA_CONFIRMATION, Map.of(
        		PmrvEmailNotificationTemplateConstants.HOME_URL, webAppProperties.getUrl(),
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink()));		
	}
    
    /**
     * Sends generic email notification for specified template and params
     * @param email
     * @param templateName {@link PmrvNotificationTemplateName}
     * @param params
     * 
     */
    private void notifyUser(String email, PmrvNotificationTemplateName templateName, Map<String, Object> params) {
    	EmailData<EmailNotificationTemplateData> emailInfo = EmailData.<EmailNotificationTemplateData>builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .templateName(templateName.getName())
                        .templateParams(params)
                        .build())
                .build();

        notificationEmailService.notifyRecipient(emailInfo, email);
    }

    private String constructRedirectionLink(String path, UserNotificationWithRedirectionLinkInfo.TokenParams tokenParams) {
        String token = jwtTokenService
            .generateToken(tokenParams.getJwtTokenAction(), tokenParams.getClaimValue(), tokenParams.getExpirationInterval());

        return UriComponentsBuilder
            .fromUriString(webAppProperties.getUrl())
            .path("/")
            .path(path)
            .queryParam(NavigationParams.TOKEN, token)
            .build()
            .toUriString();
    }	
}
