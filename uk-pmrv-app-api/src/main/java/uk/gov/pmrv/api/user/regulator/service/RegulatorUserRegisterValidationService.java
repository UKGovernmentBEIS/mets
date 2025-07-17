package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Service
@RequiredArgsConstructor
public class RegulatorUserRegisterValidationService {

    private final RegulatorAuthorityService regulatorAuthorityService;
    private final UserRoleTypeService userRoleTypeService;
    
    public void validate(final String userId, final CompetentAuthorityEnum ca) {
		if (regulatorAuthorityService.existsNonPendingAuthorityForCA(userId, ca)) {
			throw new BusinessException(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED);
		}
		
		if(regulatorAuthorityService.existsAuthorityNotForCA(userId, ca)) {
			throw new BusinessException(ErrorCode.AUTHORITY_EXISTS_FOR_DIFFERENT_ROLE_TYPE_OR_CA);
		}
		
		userRoleTypeService.validateUserRoleTypeIsEmpty(userId);
	}
}
