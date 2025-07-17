package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserSecuritySetupService {

    private final UserNotificationService userNotificationService;
    private final UserAuthService userAuthService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
    private final JwtTokenService jwtTokenService;

    public void requestTwoFactorAuthChange(AppUser currentUser, String accessToken, String otp) {
        // Validate otp
        userAuthService.validateAuthenticatedUserOtp(otp, accessToken);
        long expirationInMinutes = jwtProperties.getClaim().getChange2faExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
        		PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
        ));

        // Send email with token
        userNotificationService.notifyUserWithLink(
            UserNotificationWithRedirectionLinkInfo.builder()
                .templateName(PmrvNotificationTemplateName.CHANGE_2FA)
                .userEmail(currentUser.getEmail())
                .notificationParams(notificationParams)
                .linkParamName(PmrvEmailNotificationTemplateConstants.CHANGE_2FA_LINK)
                .linkPath(NavigationOutcomes.CHANGE_2FA_URL)
                .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                    .jwtTokenAction(JwtTokenAction.CHANGE_2FA)
                    .claimValue(currentUser.getEmail())
                    .expirationInterval(expirationInMinutes)
                    .build()
                )
                .build()
        );
    }

    public void deleteOtpCredentials(TokenDTO tokenDTO) {
        // Validate token and get email
        String userEmail = jwtTokenService.resolveTokenActionClaim(tokenDTO.getToken(), JwtTokenAction.CHANGE_2FA);

        // Delete otp credentials
        userAuthService.deleteOtpCredentialsByEmail(userEmail);
    }
    
    public void resetUser2Fa(String userId) {
    	userAuthService.deleteOtpCredentials(userId);
        userNotificationService.notifyUserReset2Fa(userId);
    }
}
