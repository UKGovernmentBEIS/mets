package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.token.JwtTokenService;
import uk.gov.pmrv.api.token.JwtProperties;
import uk.gov.pmrv.api.token.JwtTokenActionEnum;
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

    public void requestTwoFactorAuthChange(PmrvUser currentUser, String accessToken, String otp) {
        // Validate otp
        userAuthService.validateAuthenticatedUserOtp(otp, accessToken);
        long expirationInMinutes = jwtProperties.getClaim().getChange2faExpIntervalMinutes();

        Map<String, Object> notificationParams = new HashMap<>(Map.of(
                EmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
                EmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
        ));

        // Send email with token
        userNotificationService.notifyUserWithLink(
            UserNotificationWithRedirectionLinkInfo.builder()
                .templateName(NotificationTemplateName.CHANGE_2FA)
                .userEmail(currentUser.getEmail())
                .notificationParams(notificationParams)
                .linkParamName(EmailNotificationTemplateConstants.CHANGE_2FA_LINK)
                .linkPath(NavigationOutcomes.CHANGE_2FA_URL)
                .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
                    .jwtTokenAction(JwtTokenActionEnum.CHANGE_2FA)
                    .claimValue(currentUser.getEmail())
                    .expirationInterval(expirationInMinutes)
                    .build()
                )
                .build()
        );
    }

    public void deleteOtpCredentials(TokenDTO tokenDTO) {
        // Validate token and get email
        String userEmail = jwtTokenService.resolveTokenActionClaim(tokenDTO.getToken(), JwtTokenActionEnum.CHANGE_2FA);

        // Delete otp credentials
        userAuthService.deleteOtpCredentialsByEmail(userEmail);
    }
    
    public void resetUser2Fa(String userId) {
    	userAuthService.deleteOtpCredentials(userId);
        userNotificationService.notifyUserReset2Fa(userId);
    }
}
