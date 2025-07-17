package uk.gov.pmrv.api.user.operator.service;

import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.Set;

public interface OperatorRoleCodeAcceptInvitationService {

    UserInvitationStatus acceptInvitation(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO);

    Set<String> getRoleCodes();
}
