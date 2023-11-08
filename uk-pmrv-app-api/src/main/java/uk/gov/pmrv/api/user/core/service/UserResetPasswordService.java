package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.mail.config.property.NotificationProperties;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.token.JwtTokenService;
import uk.gov.pmrv.api.token.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.NavigationOutcomes;
import uk.gov.pmrv.api.token.JwtProperties;
import uk.gov.pmrv.api.user.core.domain.dto.ResetPasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserNotificationWithRedirectionLinkInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserResetPasswordService {
	
	private final UserNotificationService userNotificationService;
    private final UserAuthService userAuthService;
    private final JwtProperties jwtProperties;
    private final NotificationProperties notificationProperties;
	private final JwtTokenService jwtTokenService;

	public void sendResetPasswordEmail(String email) {
		long expirationInMinutes = jwtProperties.getClaim().getResetPasswordExpIntervalMinutes();
		Optional<UserInfoDTO> user = userAuthService.getUserByEmail(email);
		
		 if (user.isPresent()) {
			 Map<String, Object> notificationParams = new HashMap<>(Map.of(
		                EmailNotificationTemplateConstants.CONTACT_REGULATOR, notificationProperties.getEmail().getContactUsLink(),
		                EmailNotificationTemplateConstants.EXPIRATION_MINUTES, expirationInMinutes
		        ));
			 
			 userNotificationService.notifyUserWithLink(
		                UserNotificationWithRedirectionLinkInfo.builder()
		                        .templateName(NotificationTemplateName.RESET_PASSWORD_REQUEST)
		                        .userEmail(email)
		                        .notificationParams(notificationParams)
		                        .linkParamName(EmailNotificationTemplateConstants.RESET_PASSWORD_LINK)
		                        .linkPath(NavigationOutcomes.RESET_PASSWORD_URL)
		                        .tokenParams(UserNotificationWithRedirectionLinkInfo.TokenParams.builder()
		                                .jwtTokenAction(JwtTokenActionEnum.RESET_PASSWORD)
		                                .claimValue(email)
		                                .expirationInterval(jwtProperties.getClaim().getResetPasswordExpIntervalMinutes())
		                                .build()
		                        )
		                        .build()
		        ); 
		 } else {
			 log.error(String.format("Reset Password Email requested for non existing user. Email: %s",  email));
		 }
	}

	public String verifyToken(String token) {
		return jwtTokenService.resolveTokenActionClaim(token, JwtTokenActionEnum.RESET_PASSWORD);
	}

	public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
		String email = verifyToken(resetPasswordDTO.getToken());
		userAuthService.resetPassword(
				email, resetPasswordDTO.getOtp(), resetPasswordDTO.getPassword());	
		
		Optional<UserInfoDTO> user = userAuthService.getUserByEmail(email);
		if (user.isPresent()) {
			userNotificationService.notifyUserPasswordReset(user.get().getUserId());
		}
	}

}
