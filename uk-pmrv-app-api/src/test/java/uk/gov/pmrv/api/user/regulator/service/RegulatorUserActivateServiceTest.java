package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorUserActivateServiceTest {

    @InjectMocks
    private RegulatorUserActivateService regulatorUserAcceptInvitationService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;
    
    @Mock
    private RegulatorUserRegisterValidationService regulatorUserRegisterValidationService;

    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;
    
    @Test
    void acceptAuthorityAndActivateInvitedUser() {
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("invitationToken")
            .password("password")
            .build();

        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .userId("userId")
            .build();

        final UserInfoDTO invitee = UserInfoDTO.builder().firstName("invitee").email("email").build();
        final UserInfoDTO inviter = UserInfoDTO.builder().firstName("inviter").build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(
        		invitedUserCredentialsDTO.getInvitationToken()))
            .thenReturn(authorityInfo);
        when(regulatorAuthorityService.acceptAuthority(1L)).thenReturn(
            Authority.builder().userId("userId").createdBy("creator").build());
        when(userAuthService.getUserByUserId("userId")).thenReturn(invitee);
        when(userAuthService.getUserByUserId("creator")).thenReturn(inviter);

        regulatorUserAcceptInvitationService.acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);

        verify(regulatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        verify(regulatorUserRegisterValidationService, times(1)).validate(authorityInfo.getUserId(), authorityInfo.getCompetentAuthority());
        verify(regulatorAuthorityService, times(1)).acceptAuthority(authorityInfo.getId());
        verify(userRoleTypeService, times(1)).createUserRoleTypeOrThrowExceptionIfExists(authorityInfo.getUserId(), RoleTypeConstants.REGULATOR);
        verify(userAuthService, times(1)).enableUserAndSetPassword("userId", "password");
        verify(userAuthService, times(2)).getUserByUserId("userId");
        verify(userAuthService, times(1)).getUserByUserId("creator");
        verify(regulatorUserNotificationGateway, times(1)).notifyInviteeAcceptedInvitation(invitee);
        verify(regulatorUserNotificationGateway, times(1)).notifyInviterAcceptedInvitation(invitee, inviter);
    }
    
    @Test
    void acceptAuthorityForRegisteredRegulatorInvitedUser() {
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        final UserInfoDTO invitee = UserInfoDTO.builder().firstName("invitee").email("email").build();
        final UserInfoDTO inviter = UserInfoDTO.builder().firstName("inviter").build();

		when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority("token"))
				.thenReturn(authorityInfo);
        when(regulatorAuthorityService.acceptAuthority(1L)).thenReturn(
            Authority.builder().userId("userId").createdBy("creator").build());
        when(userAuthService.getUserByUserId("userId")).thenReturn(invitee);
        when(userAuthService.getUserByUserId("creator")).thenReturn(inviter);

        regulatorUserAcceptInvitationService.acceptAuthorityForRegisteredRegulatorInvitedUser("token");

        verify(regulatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority("token");
        verify(regulatorAuthorityService, times(1)).acceptAuthority(authorityInfo.getId());
        verify(userRoleTypeService, times(1)).createUserRoleTypeOrThrowExceptionIfExists(authorityInfo.getUserId(), RoleTypeConstants.REGULATOR);
        verify(userAuthService, times(1)).getUserByUserId("userId");
        verify(userAuthService, times(1)).getUserByUserId("creator");
        verify(regulatorUserNotificationGateway, times(1)).notifyInviteeAcceptedInvitation(invitee);
        verify(regulatorUserNotificationGateway, times(1)).notifyInviterAcceptedInvitation(invitee, inviter);
    }

}