package uk.gov.pmrv.api.user.operator.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.Optional;

import static uk.gov.netz.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;

@Service
@RequiredArgsConstructor
public class OperatorUserInvitationService {

	private final OperatorUserAuthService operatorUserAuthService;
    private final UserRoleTypeService userRoleTypeService;
    private final OperatorUserRegistrationService operatorUserRegistrationService;
    private final ExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void inviteUserToAccount(Long accountId, OperatorUserInvitationDTO userRegistrationDTO, AppUser currentUser) {
    	// prevalidations
        validateRequesterUserCapabilityToAddOthersToAccount(currentUser, userRegistrationDTO.getRoleCode());
        
        Optional<String> userIdOpt = operatorUserAuthService.getUserIdByEmail(userRegistrationDTO.getEmail());

        userIdOpt.ifPresentOrElse(
				userId -> existingOperatorUserInvitationService.addExistingUserToAccount(userRegistrationDTO, accountId,
						userId, currentUser),
            () -> operatorUserRegistrationService.registerUserToAccount(userRegistrationDTO, accountId, currentUser));
    }

    private void validateRequesterUserCapabilityToAddOthersToAccount(AppUser currentUser, String roleCode) {
        // Regulator user can only add operator administrator users to an account
        if (userRoleTypeService.isUserRegulator(currentUser.getUserId()) && !OPERATOR_ADMIN_ROLE_CODE.equalsIgnoreCase(roleCode)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_REGULATOR_NOT_ALLOWED_TO_ADD_OPERATOR_ROLE_TO_ACCOUNT);
        }
    }

}
