package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDelegator {

    private final List<OperatorRoleCodeAcceptInvitationService> operatorRoleCodeAcceptInvitationServices;

    @Transactional
	public UserInvitationStatus acceptInvitation(OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO,
			String roleCode) {
		return operatorRoleCodeAcceptInvitationServices.stream()
				.filter(service -> service.getRoleCodes().contains(roleCode))
				.findAny()
				.map(service -> service.acceptInvitation(operatorUserWithAuthorityDTO))
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_REGISTRATION_FAILED_500));
    }

}
