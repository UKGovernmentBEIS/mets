package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserAcceptInvitationMapper;

@Service
@RequiredArgsConstructor
public class OperatorUserAcceptInvitationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper;
    private final AccountQueryService accountQueryService;
    private final OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator;

    @Transactional
    public OperatorInvitedUserInfoDTO acceptInvitation(String invitationToken) {
        final AuthorityInfoDTO authorityInfo =
            operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());
        
        final OperatorUserDTO operatorUser = operatorUserAuthService.getOperatorUserById(authorityInfo.getUserId());

		final OperatorUserWithAuthorityDTO operatorUserWithAuthorityDTO = operatorUserAcceptInvitationMapper
				.toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo,
						accountQueryService.getAccountName(authorityInfo.getAccountId()));

        final UserInvitationStatus invitationStatus = operatorRoleCodeAcceptInvitationServiceDelegator
            .acceptInvitation(operatorUserWithAuthorityDTO, authorityInfo.getCode());

        return operatorUserAcceptInvitationMapper
            .toOperatorInvitedUserInfoDTO(operatorUserWithAuthorityDTO, authorityInfo.getCode(), invitationStatus);
    }
}
