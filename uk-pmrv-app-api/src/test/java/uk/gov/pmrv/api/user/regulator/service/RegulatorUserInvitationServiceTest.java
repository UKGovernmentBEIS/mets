package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.authorization.regulator.transform.RegulatorPermissionsAdapter;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;

@ExtendWith(MockitoExtension.class)
class RegulatorUserInvitationServiceTest {

    @InjectMocks
    private RegulatorUserInvitationService service;

    @Mock
    private RegulatorUserAuthService regulatorUserAuthService;

    @Mock
    private RegulatorAuthorityService regulatorAuthorityService;

    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;
    
    @Mock
    private RegulatorUserRegisterValidationService regulatorUserRegisterValidationService;
    
    @Mock
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;
    
    @Mock
    private RegulatorUserActivateService regulatorUserActivateService;

    @Mock
    private RegulatorPermissionsAdapter regulatorPermissionsAdapter;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void inviteRegulatorUser_existing_user() {
        String userId = "userId";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        AppUser authUser = AppUser.builder()
            .userId("authUser")
            .roleType(RoleTypeConstants.REGULATOR)
            .authorities(List.of(AppAuthority.builder().competentAuthority(ca).build()))
            .build();

        String authorityUuid = "uuid";
        RegulatorInvitedUserDTO regulatorInvitedUser = createInvitedUser();
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
        
        UserInfoDTO userInfoDTO = UserInfoDTO.builder().userId(userId).build();
        
		when(regulatorUserAuthService.getUserByEmail(regulatorInvitedUser.getUserDetails().getEmail()))
				.thenReturn(Optional.of(userInfoDTO));
        when(regulatorUserAuthService.registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature))
            .thenReturn(userId);
        when(regulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(Map.of(MANAGE_USERS_AND_CONTACTS, EXECUTE))).thenReturn(List.of(Permission.PERM_CA_USERS_EDIT));
        when(regulatorAuthorityService
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT)))
            .thenReturn(authorityUuid);

        //invoke
        service.inviteRegulatorUser(regulatorInvitedUser,signature,  authUser);

        //verify
        verify(regulatorUserAuthService, times(1)).getUserByEmail(regulatorInvitedUser.getUserDetails().getEmail());
        verify(regulatorUserRegisterValidationService, times(1)).validate(userId, ca);
        verify(regulatorUserAuthService, times(1))
        .registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature);
        verify(regulatorAuthorityService, times(1))
            .createRegulatorAuthorityPermissions(authUser, userId, ca, List.of(Permission.PERM_CA_USERS_EDIT));
        verify(regulatorUserNotificationGateway, times(1))
            .notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);
    }
    
    @Test
    void acceptInvitation_enabled_with_password() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder()
            .email(userEmail)
            .enabled(true)
            .build();

        InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
        		.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED)
        		.build();

		when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken))
				.thenReturn(authorityInfo);
		when(userAuthService.hasUserPassword(authorityInfo.getUserId())).thenReturn(true);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        InvitedUserInfoDTO actualInvitedUserInfo = service.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
        verify(userAuthService, times(1)).hasUserPassword(authorityInfo.getUserId());
		verify(regulatorUserActivateService, times(1))
				.acceptAuthorityForRegisteredRegulatorInvitedUser(invitationToken);
    }
    
    @Test
    void acceptInvitation_enabled_no_password() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder()
            .email(userEmail)
            .enabled(true)
            .build();

        InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
        		.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY)
        		.build();

		when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken))
				.thenReturn(authorityInfo);
		when(userAuthService.hasUserPassword(authorityInfo.getUserId())).thenReturn(false);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        InvitedUserInfoDTO actualInvitedUserInfo = service.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
        verify(userAuthService, times(1)).hasUserPassword(authorityInfo.getUserId());
		verify(regulatorUserActivateService, never())
				.acceptAuthorityForRegisteredRegulatorInvitedUser(invitationToken);
    }
    
    @Test
    void acceptInvitation_not_enabled() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .userId("userId")
            .build();

        RegulatorUserDTO regulatorUser = RegulatorUserDTO.builder()
            .email(userEmail)
            .enabled(false)
            .build();

        InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
        		.invitationStatus(UserInvitationStatus.PENDING_TO_REGISTERED_SET_PASSWORD_ONLY)
        		.build();

        when(regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId())).thenReturn(regulatorUser);

        InvitedUserInfoDTO actualInvitedUserInfo = service.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(regulatorUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(regulatorUserAuthService, times(1)).getRegulatorUserById(authorityInfo.getUserId());
		verify(regulatorUserRegisterValidationService, times(1)).validate(authorityInfo.getUserId(),
				authorityInfo.getCompetentAuthority());
        verifyNoInteractions(regulatorUserActivateService);
    }

    private RegulatorInvitedUserDTO createInvitedUser() {
        RegulatorInvitedUserDTO invitedUser =
            RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                    .firstName("fn")
                    .lastName("ln")
                    .email("em@em.gr")
                    .jobTitle("title")
                    .phoneNumber("210000")
                    .build())
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, EXECUTE))
                .build();
        return invitedUser;
    }

}
