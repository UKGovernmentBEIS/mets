package uk.gov.pmrv.api.user.verifier.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;

@Service
@RequiredArgsConstructor
public class VerifierUserManagementService {
	
	private final VerifierAuthorityService verifierAuthorityService;
	private final VerifierUserAuthService verifierUserAuthService;
	private final UserSecuritySetupService userSecuritySetupService;

	public VerifierUserDTO getVerifierUserById(AppUser appUser, String userId) {
        validateUserBasedOnAuthUserRole(appUser, userId);
		return verifierUserAuthService.getVerifierUserById(userId);
	}

	public void updateVerifierUserById(AppUser appUser, String userId, VerifierUserDTO verifierUserDTO) {
	    validateUserBasedOnAuthUserRole(appUser, userId);
		verifierUserAuthService.updateVerifierUser(verifierUserDTO);
	}

	public void updateCurrentVerifierUser(VerifierUserDTO verifierUserDTO) {
		verifierUserAuthService.updateVerifierUser(verifierUserDTO);
	}
	
	public void resetVerifier2Fa(AppUser appUser, String userId) {
		validateUserBasedOnAuthUserRole(appUser, userId);
		userSecuritySetupService.resetUser2Fa(userId);
	}

	private void validateUserBasedOnAuthUserRole(AppUser appUser, String userId) {
        switch (appUser.getRoleType()) {
            case RoleTypeConstants.REGULATOR:
                validateUserIsVerifier(userId);
                break;
            case RoleTypeConstants.VERIFIER:
                validateUserHasAccessToVerificationBody(appUser, userId);
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("User with role type %s can not access verifier user", appUser.getRoleType()));
        }
    }

	/** Validate if user has access to queried user's verification body. */
	private void validateUserHasAccessToVerificationBody(AppUser appUser, String userId) {
		Long verificationBodyId = appUser.getVerificationBodyId();

		if(!verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verificationBodyId)){
			throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY);
		}
	}

	private void validateUserIsVerifier(String userId) {
        if(!verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER);
        }
    }
}
