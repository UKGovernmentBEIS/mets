package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserActivateServiceTest {

    @InjectMocks
    private VerifierUserActivateService verifierUserAcceptInvitationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private VerifierUserTokenVerificationService verifierUserTokenVerificationService;

    @Mock
    private VerifierAuthorityService verifierAuthorityService;
    
    @Mock
    private VerifierUserRegisterValidationService verifierUserRegisterValidationService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Test
    void acceptAuthorityAndActivateInvitedUser() {
        String inviterUserId = "inviterUserId";
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();
        AuthorityInfoDTO authorityInfoDTO = AuthorityInfoDTO.builder()
            .id(1L)
            .userId("userId")
            .code("verifier")
            .verificationBodyId(1L)
            .build();
        UserInfoDTO invitee = UserInfoDTO.builder().userId(authorityInfoDTO.getUserId()).build();
        UserInfoDTO inviter = UserInfoDTO.builder().userId(inviterUserId).build();

        when(verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken()))
                .thenReturn(authorityInfoDTO);
        when(verifierAuthorityService.acceptAuthority(authorityInfoDTO.getId()))
                .thenReturn(Authority.builder().userId("userId").createdBy(inviterUserId).build());
        when(userAuthService.getUserByUserId(authorityInfoDTO.getUserId()))
                .thenReturn(invitee);
        when(userAuthService.getUserByUserId(inviterUserId))
                .thenReturn(inviter);

        // Invoke
        verifierUserAcceptInvitationService.acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);

        // Verify
        verify(verifierUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        verify(verifierUserRegisterValidationService, times(1))
        	.validate(authorityInfoDTO.getUserId(), 1L);
        verify(verifierAuthorityService, times(1))
                .acceptAuthority(authorityInfoDTO.getId());
        verify(userRoleTypeService, times(1))
        	.createUserRoleTypeOrThrowExceptionIfExists(authorityInfoDTO.getUserId(), RoleTypeConstants.VERIFIER);
        verify(userAuthService, times(2))
                .getUserByUserId(authorityInfoDTO.getUserId());
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviteeAcceptedInvitation(invitee);
        verify(userAuthService, times(1))
                .getUserByUserId(inviterUserId);
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviterAcceptedInvitation(invitee, inviter);
        verify(userAuthService, times(1)).enableUserAndSetPassword("userId", invitedUserCredentialsDTO.getPassword());
    }
    
    @Test
    void acceptAuthorityForRegisteredVerifierInvitedUser() {
        String inviterUserId = "inviterUserId";
        AuthorityInfoDTO authorityInfoDTO = AuthorityInfoDTO.builder()
            .id(1L)
            .userId("userId")
            .code("verifier")
            .verificationBodyId(1L)
            .build();
        UserInfoDTO invitee = UserInfoDTO.builder().userId(authorityInfoDTO.getUserId()).build();
        UserInfoDTO inviter = UserInfoDTO.builder().userId(inviterUserId).build();

        when(verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority("token"))
                .thenReturn(authorityInfoDTO);
        when(verifierAuthorityService.acceptAuthority(authorityInfoDTO.getId()))
                .thenReturn(Authority.builder().userId("userId").createdBy(inviterUserId).build());
        when(userAuthService.getUserByUserId(authorityInfoDTO.getUserId()))
                .thenReturn(invitee);
        when(userAuthService.getUserByUserId(inviterUserId))
                .thenReturn(inviter);

        // Invoke
        verifierUserAcceptInvitationService.acceptAuthorityForRegisteredVerifierInvitedUser("token");

        // Verify
        verify(verifierUserTokenVerificationService, times(1))
                .verifyInvitationTokenForPendingAuthority("token");
        verify(verifierAuthorityService, times(1))
                .acceptAuthority(authorityInfoDTO.getId());
        verify(userRoleTypeService, times(1))
    		.createUserRoleTypeOrThrowExceptionIfExists(authorityInfoDTO.getUserId(), RoleTypeConstants.VERIFIER);
        verify(userAuthService, times(1))
                .getUserByUserId(authorityInfoDTO.getUserId());
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviteeAcceptedInvitation(invitee);
        verify(userAuthService, times(1))
                .getUserByUserId(inviterUserId);
        verify(verifierUserNotificationGateway, times(1))
                .notifyInviterAcceptedInvitation(invitee, inviter);
        verify(userAuthService, never()).enableUserAndSetPassword(Mockito.anyString(), Mockito.anyString());
    }

}