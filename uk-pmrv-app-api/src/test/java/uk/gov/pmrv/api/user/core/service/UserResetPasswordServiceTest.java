package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.token.JwtProperties;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.pmrv.api.notification.mail.constants.PmrvEmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.user.core.domain.dto.ResetPasswordDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserResetPasswordServiceTest {

	@InjectMocks
    private UserResetPasswordService userResetPasswordService;

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
    
    private static final String EMAIL = "email@email.com";
    private static final String TOKEN = "test.jwt.token";
    private static final String PASSWORD = "Password123!";
    private static final String OTP = "123456!";
    
    
    @Test
    void sendVerificationEmail() {
    	String contactUsLink = "url.com/contact-us";
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        NotificationProperties.Email notificationEmail = mock(NotificationProperties.Email.class);
        long expirationInterval = 20L;
        UserInfoDTO user = buildMockUser();

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getResetPasswordExpIntervalMinutes()).thenReturn(expirationInterval);
        when(notificationProperties.getEmail()).thenReturn(notificationEmail);
        when(notificationEmail.getContactUsLink()).thenReturn(contactUsLink);
        when(userAuthService.getUserByEmail(anyString())).thenReturn(Optional.of(user));

        userResetPasswordService.sendResetPasswordEmail(EMAIL);

        // verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(1)).notifyUserWithLink(notificationInfoCaptor.capture());

        UserNotificationWithRedirectionLinkInfo notificationInfo = notificationInfoCaptor.getValue();

        assertThat(notificationInfo.getTemplateName()).isEqualTo(PmrvNotificationTemplateName.RESET_PASSWORD_REQUEST);
        assertThat(notificationInfo.getUserEmail()).isEqualTo(EMAIL);
        assertThat(notificationInfo.getLinkParamName()).isEqualTo(PmrvEmailNotificationTemplateConstants.RESET_PASSWORD_LINK);
        assertThat(notificationInfo.getLinkPath()).isEqualTo(NavigationOutcomes.RESET_PASSWORD_URL);
        assertThat(notificationInfo.getNotificationParams())
        .isEqualTo(Map.of(
        		PmrvEmailNotificationTemplateConstants.CONTACT_REGULATOR, contactUsLink,
                PmrvEmailNotificationTemplateConstants.EXPIRATION_MINUTES, 20L));
        assertThat(notificationInfo.getTokenParams())
            .isEqualTo(expectedTokenParams(EMAIL, expirationInterval));
    }
    
    @Test
    void sendVerificationEmail_email_not_exists() {
        JwtProperties.Claim claim = mock(JwtProperties.Claim.class);
        long expirationInterval = 20L;

        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getResetPasswordExpIntervalMinutes()).thenReturn(expirationInterval);
        when(userAuthService.getUserByEmail(anyString())).thenReturn(Optional.empty());

        userResetPasswordService.sendResetPasswordEmail(EMAIL);

        // verify
        ArgumentCaptor<UserNotificationWithRedirectionLinkInfo> notificationInfoCaptor =
            ArgumentCaptor.forClass(UserNotificationWithRedirectionLinkInfo.class);
        verify(userNotificationService, times(0)).notifyUserWithLink(notificationInfoCaptor.capture());
    }
    
    @Test
	void verifyRegistrationToken() {
    	when(jwtTokenService.resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD))
    		.thenReturn(EMAIL);
    	
    	String result = userResetPasswordService.verifyToken(TOKEN);
    	
    	// verify
    	assertThat(result).isEqualTo(EMAIL);
    	
    	verify(jwtTokenService, times(1)).resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD);
	}
	
	@Test
	void verifyRegistrationToken_link_expired() {	
		when(jwtTokenService.resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD))
    	    .thenThrow(new BusinessException(ErrorCode.VERIFICATION_LINK_EXPIRED));
    	
    	BusinessException ex = assertThrows(BusinessException.class, () -> {
    		userResetPasswordService.verifyToken(TOKEN);
    	});
    	assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_LINK_EXPIRED);

    	verify(jwtTokenService, times(1)).resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD);
	}
	
	@Test
	void resetPassword() {
		UserInfoDTO user = buildMockUser();
		ResetPasswordDTO resetPasswordDTO = buildMockPasswordDTO();
		
    	when(jwtTokenService.resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD))
    		.thenReturn(EMAIL);
    	when(userAuthService.getUserByEmail(anyString())).thenReturn(Optional.of(user));
    	
    	userResetPasswordService.resetPassword(resetPasswordDTO);
    	
    	// verify
    	ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<String> argument3 = ArgumentCaptor.forClass(String.class);
    	verify(userAuthService, times(1)).resetPassword(
    			argument.capture(), argument2.capture(), argument3.capture());

        assertThat(argument.getValue()).isEqualTo(EMAIL);
        assertThat(argument2.getValue()).isEqualTo(OTP);
        assertThat(argument3.getValue()).isEqualTo(PASSWORD);
        ArgumentCaptor<String> argument4 = ArgumentCaptor.forClass(String.class);
        verify(userNotificationService, times(1)).notifyUserPasswordReset(argument4.capture());
        assertThat(argument4.getValue()).isEqualTo(EMAIL);
	}
	
	@Test
	void resetPassword_user_not_exist() {
		ResetPasswordDTO resetPasswordDTO = buildMockPasswordDTO();
		
    	when(jwtTokenService.resolveTokenActionClaim(TOKEN, JwtTokenAction.RESET_PASSWORD))
    		.thenReturn(EMAIL);
    	doThrow(new BusinessException(ErrorCode.USER_NOT_EXIST))
	       .when(userAuthService)
	       .resetPassword(anyString(), anyString(), anyString());
    	
    	BusinessException ex = assertThrows(BusinessException.class, () -> {
    		userResetPasswordService.resetPassword(resetPasswordDTO);
    	});
    	assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_EXIST);
    	
    	ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
    	ArgumentCaptor<String> argument3 = ArgumentCaptor.forClass(String.class);
    	verify(userAuthService, times(1)).resetPassword(
    			argument.capture(), argument2.capture(), argument3.capture());

        ArgumentCaptor<String> argument4 = ArgumentCaptor.forClass(String.class);
        verify(userNotificationService, times(0)).notifyUserPasswordReset(argument4.capture());
	}
	
	private UserInfoDTO buildMockUser() {
		return UserInfoDTO.builder()
        		.email(EMAIL)
        		.userId(EMAIL)
        		.build();
	}
	
	private ResetPasswordDTO buildMockPasswordDTO() {
		return ResetPasswordDTO.builder()
        		.token(TOKEN)
        		.otp(OTP)
        		.password(PASSWORD)
        		.build();
	}
	
	private UserNotificationWithRedirectionLinkInfo.TokenParams expectedTokenParams(String claimValue, Long expirationInterval) {
        return UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
            .jwtTokenAction(JwtTokenAction.RESET_PASSWORD)
            .claimValue(claimValue)
            .expirationInterval(expirationInterval)
            .build();
    }
}
