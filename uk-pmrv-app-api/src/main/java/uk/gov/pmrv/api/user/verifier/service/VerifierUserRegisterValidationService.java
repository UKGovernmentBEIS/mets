package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class VerifierUserRegisterValidationService {

    private final VerifierAuthorityService verifierAuthorityService;
    private final UserRoleTypeService userRoleTypeService;
    
    public void validate(final String userId, final Long vbId) {
    	if (verifierAuthorityService.existsNonPendingAuthorityForVB(userId, vbId)) {
			throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		}
    	
    	if (verifierAuthorityService.existsAuthorityNotForVB(userId, vbId)) {
			throw new BusinessException(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_OR_VB);
		}
		
		userRoleTypeService.validateUserRoleTypeIsEmpty(userId);
	}
}
