package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserUpdateDTO;

@Service
@RequiredArgsConstructor
public class RegulatorUserManagementService {

    private final RegulatorUserAuthService regulatorUserAuthService;

    private final RegulatorAuthorityService regulatorAuthorityService;
    
    private final UserSecuritySetupService userSecuritySetupService;

    public RegulatorUserDTO getRegulatorUserByUserId(PmrvUser pmrvUser, String userId) {
        // Validate
        validateRegulatorUser(pmrvUser, userId);

        return regulatorUserAuthService.getRegulatorUserById(userId);
    }

    @Transactional
    public void updateRegulatorUserByUserId(PmrvUser pmrvUser, String userId, RegulatorUserDTO regulatorUserUpdateDTO, FileDTO signature) {
        validateRegulatorUser(pmrvUser, userId);

        regulatorUserAuthService.updateRegulatorUser(userId, regulatorUserUpdateDTO, signature);
    }

    @Transactional
    public void updateCurrentRegulatorUser(PmrvUser pmrvUser, RegulatorUserUpdateDTO regulatorUserUpdateDTO, FileDTO signature) {
        regulatorUserAuthService.updateRegulatorUser(pmrvUser.getUserId(), regulatorUserUpdateDTO.getUser(), signature);
    }

	public void resetRegulator2Fa(PmrvUser pmrvUser, String userId) {
		validateRegulatorUser(pmrvUser, userId);
		userSecuritySetupService.resetUser2Fa(userId);
	}
	
	private void validateRegulatorUser(PmrvUser pmrvUser, String userId) {
		if (!regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, pmrvUser.getCompetentAuthority())) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
	}
}
