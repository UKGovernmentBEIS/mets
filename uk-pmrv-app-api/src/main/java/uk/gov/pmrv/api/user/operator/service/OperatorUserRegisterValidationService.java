package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class OperatorUserRegisterValidationService {

    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final UserRoleTypeService userRoleTypeService;
    
    public void validateRegister(final String userId) {
		if(operatorAuthorityQueryService.existsAuthorityNotForAccount(userId)) {
			throw new BusinessException(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_THAN_OPERATOR);
		}
		
		userRoleTypeService.validateUserRoleTypeIsOfTypeOrNotExist(userId, RoleTypeConstants.OPERATOR);
	}
    
    public void validateRegisterForAccount(final String userId, final Long accountId) {
    	validateRegister(userId);
    	
		if (operatorAuthorityQueryService.existsNonPendingAuthorityForAccount(userId, accountId)) {
			throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		}
	}
    
}
