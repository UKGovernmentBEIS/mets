package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecuritySetupServiceTest {

    @InjectMocks
    private UserSecuritySetupService userSecuritySetupService;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private NotificationProperties notificationProperties;

    @Test
    void requestTwoFactorAuthChange() {
        AppUser appUser = AppUser.builder().email("email").build();
        String contactUsLink = "/contact-us";
        String otp = "otp";
        String accessToken = "accessToken";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        Long expirationInterval = 60L;
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getChange2faExpIntervalMinutes()).thenReturn(expirationInterval);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);

        userSecuritySetupService.requestTwoFactorAuthChange(appUser, accessToken, otp);

        verify(userAuthService, times(1)).validateAuthenticatedUserOtp(otp, accessToken);
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.CHANGE_2FA);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(appUser.getEmail());
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(PmrvEmailNotificationTemplateConstants.CHANGE_2FA_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.CHANGE_2FA_URL);
        assertThat(notificationInfo.getNotificationParams()).hasSize(2);
        assertThat(notificationInfo.getNotificationParams())
                .isEqualTo(Map.of(
                		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink,
                		PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, 60L));
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedTokenParams(appUser.getEmail(), expirationInterval));
    }

    @Test
    void deleteOtpCredentials() {
        TokenDTO token = TokenDTO.builder().token("token").build();
        String userEmail = "email";

        when(jwtTokenService.resolveTokenActionClaim(token.getToken(), JwtTokenAction.CHANGE_2FA)).thenReturn(userEmail);

        userSecuritySetupService.deleteOtpCredentials(token);

        verify(userAuthService, times(1)).deleteOtpCredentialsByEmail(userEmail);
    }
    
    @Test
    void resetUser2Fa() {
        String userId = "123abcd";
        
        userSecuritySetupService.resetUser2Fa(userId);

        verify(userAuthService, times(1)).deleteOtpCredentials(userId);
        verify(userNotificationService, times(1)).notifyUserReset2Fa(userId);
    }

    private UserNotificationWithRedirectionLinkInfo.TokenParams expectedTokenParams(String claimValue, Long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
            .jwtTokenAction(JwtTokenAction.CHANGE_2FA)
            .claimValue(claimValue)
            .expirationInterval(expirationInterval)
            .build();
    }
}