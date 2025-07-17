package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDefaultImpl implements OperatorRoleCodeAcceptInvitationService {

    private final RoleService roleService;
    private final UserAuthService userAuthService;
    private final OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO) {
    	if(operatorUserWithAuthorityDTO.isEnabled()) {
    		if(userAuthService.hasUserPassword(operatorUserWithAuthorityDTO.getUserId())) {
            	operatorUserRegisteredAcceptInvitationService
						.acceptAuthorityAndNotify(operatorUserWithAuthorityDTO.getUserAuthorityId());
                return UserInvitationStatus.ACCEPTED;
            } else {
                return UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY;
            }
    	} else {
    		return UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM;
    	}
    }

    @Override
    public Set<String> getRoleCodes() {
        return roleService.getCodesByType(RoleTypeConstants.OPERATOR).stream()
            .filter(roleCode -> !AuthorityConstants.EMITTER_CONTACT.equals(roleCode))
            .collect(Collectors.toSet());
    }

}
