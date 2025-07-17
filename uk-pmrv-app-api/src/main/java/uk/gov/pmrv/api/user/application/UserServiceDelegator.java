package uk.gov.pmrv.api.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.core.service.UserService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceDelegator {

	private final UserRoleTypeService userRoleTypeService;
	private final UserService userService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final RegulatorUserAuthService regulatorUserAuthService;
    private final VerifierUserAuthService verifierUserAuthService;

	public UserDTO getUserById(String userId) {
		final Optional<UserRoleTypeDTO> userRoleTypeOpt = userRoleTypeService.getUserRoleTypeByUserIdOpt(userId);

		return userRoleTypeOpt.map(userRoleType -> {
			switch (userRoleTypeOpt.get().getRoleType()) {
			case RoleTypeConstants.OPERATOR:
				return operatorUserAuthService.getOperatorUserById(userId);
			case RoleTypeConstants.REGULATOR:
				return regulatorUserAuthService.getRegulatorUserById(userId);
			case RoleTypeConstants.VERIFIER:
				return verifierUserAuthService.getVerifierUserById(userId);
			default:
				throw new UnsupportedOperationException(
						String.format("Unsupported role type %s", userRoleTypeOpt.get().getRoleType()));
			}
		}).orElseGet(() -> userService.getUserByUserId(userId));
	}

	public UserDTO getCurrentUserDTO(AppUser currentUser) {
		final String userId = currentUser.getUserId();
		final Optional<UserRoleTypeDTO> userRoleTypeOpt = userRoleTypeService.getUserRoleTypeByUserIdOpt(userId);

		return userRoleTypeOpt.map(userRoleType -> {
			switch (userRoleTypeOpt.get().getRoleType()) {
			case RoleTypeConstants.OPERATOR:
				return operatorUserAuthService.getOperatorUserById(userId);
			case RoleTypeConstants.REGULATOR:
				return regulatorUserAuthService.getRegulatorCurrentUser(currentUser);
			case RoleTypeConstants.VERIFIER:
				return verifierUserAuthService.getVerifierUserById(userId);
			default:
				throw new UnsupportedOperationException(
						String.format("Unsupported role type %s", userRoleTypeOpt.get().getRoleType()));
			}
		}).orElseGet(() -> userService.getUserByUserId(userId));
	}

}
