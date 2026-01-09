package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserAcceptInvitationMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserAcceptInvitationServiceTest {

    @InjectMocks
    private OperatorUserAcceptInvitationService operatorUserAcceptInvitationService;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private OperatorUserRegisterValidationService operatorUserRegisterValidationService;

    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;

    @Mock
    private OperatorUserAcceptInvitationMapper operatorUserAcceptInvitationMapper;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private OperatorRoleCodeAcceptInvitationServiceDelegator operatorRoleCodeAcceptInvitationServiceDelegator;

    @Test
    void acceptInvitation() {
        String invitationToken = "token";
        String userId = "userId";
        Long accountId = 1L;
        String authorityRoleCode = "roleCode";
        String accountInstallationName = "accountInstallationName";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder().userId(userId).accountId(accountId).code(authorityRoleCode).build();
        OperatorUserDTO operatorUser = OperatorUserDTO.builder().build();
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder().build();
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;


        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(operatorUserAuthService.getOperatorUserById(authorityInfo.getUserId())).thenReturn(operatorUser);
        when(accountQueryService.getAccountName(authorityInfo.getAccountId())).thenReturn(accountInstallationName);
        when(operatorUserAcceptInvitationMapper.toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo, accountInstallationName))
            .thenReturn(operatorUserAcceptInvitation);
        when(operatorRoleCodeAcceptInvitationServiceDelegator.acceptInvitation(operatorUserAcceptInvitation, authorityInfo.getCode()))
            .thenReturn(userInvitationStatus);

        operatorUserAcceptInvitationService.acceptInvitation(invitationToken);

        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, authorityInfo.getAccountId());
        verify(operatorUserAuthService, times(1)).getOperatorUserById(userId);
        verify(accountQueryService, times(1)).getAccountName(accountId);
        verify(operatorUserAcceptInvitationMapper, times(1)).
        toOperatorUserWithAuthorityDTO(operatorUser, authorityInfo, accountInstallationName);
        verify(operatorRoleCodeAcceptInvitationServiceDelegator, times(1))
            .acceptInvitation(operatorUserAcceptInvitation, authorityRoleCode);
        verify(operatorUserAcceptInvitationMapper, times(1))
            .toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, authorityRoleCode, userInvitationStatus);
    }

}