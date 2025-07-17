package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmitterContactAcceptInvitationService implements OperatorRoleCodeAcceptInvitationService {

    private final OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO) {
		if (operatorUserWithAuthorityDTO.isEnabled()) {
			operatorUserRegisteredAcceptInvitationService
					.acceptAuthorityAndNotify(operatorUserWithAuthorityDTO.getUserAuthorityId());
			return UserInvitationStatus.ACCEPTED;
		} else {
			return UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM_NO_PASSWORD;
		}
    }

    @Override
    public Set<String> getRoleCodes() {
        return Set.of(AuthorityConstants.EMITTER_CONTACT);
    }

}
