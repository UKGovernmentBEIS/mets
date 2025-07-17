package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class OperatorUserTokenVerificationService {
	
	private final UserAuthService userAuthService;
	private final UserRoleTypeService userRoleTypeService;
    private final JwtTokenService jwtTokenService;
    private final UserInvitationTokenVerificationService userInvitationTokenVerificationService;
    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;

	public OperatorUserTokenVerificationResult verifyRegistrationTokenAndResolveAndValidateUserExistence(String token) {
	    String userEmail = jwtTokenService.resolveTokenActionClaim(token, JwtTokenAction.USER_REGISTRATION);
	    
	    Optional<UserInfoDTO> userOpt = userAuthService.getUserByEmail(userEmail);
	    if(userOpt.isPresent()) {
	    	final String userId = userOpt.get().getUserId();
	    	operatorUserRegisterValidationService.validateRegister(userId);
    		userRoleTypeService.createUserRoleTypeOrThrowExceptionIfExists(userId, RoleTypeConstants.OPERATOR);
			return OperatorUserTokenVerificationResult.builder().email(userEmail)
					.status(OperatorUserTokenVerificationStatus.REGISTERED).build();
	    } else {
	    	return OperatorUserTokenVerificationResult.builder().email(userEmail)
					.status(OperatorUserTokenVerificationStatus.NOT_REGISTERED).build();
	    }
    }
	
    public AuthorityInfoDTO verifyInvitationTokenForPendingAuthority(String invitationToken) {
        return userInvitationTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken, JwtTokenAction.OPERATOR_INVITATION);
    }
}
