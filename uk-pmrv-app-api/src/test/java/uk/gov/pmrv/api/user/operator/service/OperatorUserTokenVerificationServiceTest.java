package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.netz.api.token.JwtTokenService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationResult;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserTokenVerificationServiceTest {
	
	@InjectMocks
	private OperatorUserTokenVerificationService cut;
	
	@Mock
	private UserAuthService userAuthService;
	
	@Mock
	private UserRoleTypeService userRoleTypeService;
	
	@Mock
	private JwtTokenService jwtTokenService;
	
	@Mock
	private UserInvitationTokenVerificationService userInvitationTokenVerificationService;
	
	@Mock
	private OperatorUserRegisterValidationService operatorUserRegisterValidationService;
	
	@Test
	void verifyRegistrationTokenAndResolveAndValidateUserExistence() {
		String token = "token";
		String userEmail = "email";
		
    	when(jwtTokenService.resolveTokenActionClaim(token, JwtTokenAction.USER_REGISTRATION))
    		.thenReturn(userEmail);
    	when(userAuthService.getUserByEmail(userEmail)).thenReturn(Optional.empty());
    	
    	OperatorUserTokenVerificationResult result = cut.verifyRegistrationTokenAndResolveAndValidateUserExistence(token);
    	
    	assertThat(result).isEqualTo(OperatorUserTokenVerificationResult.builder()
    			.email(userEmail).status(OperatorUserTokenVerificationStatus.NOT_REGISTERED)
    			.build());
    	
    	verify(userAuthService, times(1)).getUserByEmail(userEmail);
    	verifyNoInteractions(userRoleTypeService, operatorUserRegisterValidationService);
	}
	
	@Test
	void verifyRegistrationTokenAndResolveAndValidateUserExistence_user_exists() {
		String token = "token";
		String userEmail = "email";
		String userId = "userId";
		
		UserInfoDTO user = 
				UserInfoDTO.builder().userId(userId)
    				.build();
		
    	when(jwtTokenService.resolveTokenActionClaim(token, JwtTokenAction.USER_REGISTRATION))
    		.thenReturn(userEmail);
    	when(userAuthService.getUserByEmail(userEmail)).thenReturn(Optional.of(user));
    	
    	//invoke
    	cut.verifyRegistrationTokenAndResolveAndValidateUserExistence(token);

    	verify(userAuthService, times(1)).getUserByEmail(userEmail);
    	verify(operatorUserRegisterValidationService, times(1)).validateRegister(userId);
    	verify(userRoleTypeService, times(1)).createUserRoleTypeOrThrowExceptionIfExists(userId, RoleTypeConstants.OPERATOR);
	}
	
	@Test
	void verifyInvitationTokenForPendingAuthority() {
		String invitationToken = "inv";
		AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().accountId(1L).build();
		when(userInvitationTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken,
				JwtTokenAction.OPERATOR_INVITATION)).thenReturn(authorityInfo);
		
		AuthorityInfoDTO result = cut.verifyInvitationTokenForPendingAuthority(invitationToken);
		
		assertThat(result).isEqualTo(authorityInfo);
		verify(userInvitationTokenVerificationService, times(1))
				.verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenAction.OPERATOR_INVITATION);
	}
}
