package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserInvitationServiceTest {

    @InjectMocks
    private VerifierUserInvitationService verifierUserInvitationService;

    @Mock
    private VerifierUserAuthService verifierUserAuthService;

    @Mock
    private VerifierAuthorityService verifierAuthorityService;
    
    @Mock
    private VerifierUserRegisterValidationService verifierUserRegisterValidationService;

    @Mock
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Mock
    private VerifierUserTokenVerificationService verifierUserTokenVerificationService;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;
    
    @Mock
    private VerifierUserActivateService verifierUserActivateService;
    
    @Mock
    private UserAuthService userAuthService;

    @Test
    void inviteVerifierUser() {
        final Long vbId = 1L;
        final String invitedUserId = "invitedUserId";
        AppUser appUser = AppUser.builder().userId("appUser").build();
        appUser.setAuthorities(List.of(AppAuthority.builder().code("verifier_admin").verificationBodyId(vbId).build()));

        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();

        UserInfoDTO existingUser = UserInfoDTO.builder().userId(invitedUserId).build();
        
        String authorityUuid = "uuid";

		when(verifierUserAuthService.getUserByEmail(verifierUserInvitation.getEmail()))
				.thenReturn(Optional.of(existingUser));
		
        when(verifierUserAuthService.registerInvitedVerifierUser(verifierUserInvitation)).thenReturn(invitedUserId);
        when(verifierAuthorityService.createPendingAuthority(vbId, verifierUserInvitation.getRoleCode(), invitedUserId, appUser))
            .thenReturn(authorityUuid);

        //invoke
        verifierUserInvitationService.inviteVerifierUser(appUser, verifierUserInvitation);

        //verify
        verify(verifierUserAuthService, times(1)).getUserByEmail(verifierUserInvitation.getEmail());
        verify(verifierUserRegisterValidationService, times(1)).validate(invitedUserId, vbId);
        verify(verifierUserAuthService, times(1)).registerInvitedVerifierUser(verifierUserInvitation);
        verify(verifierAuthorityService, times(1))
            .createPendingAuthority(vbId, verifierUserInvitation.getRoleCode(), invitedUserId, appUser);
        verify(verifierUserNotificationGateway, times(1))
            .notifyInvitedUser(verifierUserInvitation, authorityUuid);
    }

    @Test
    void inviteVerifierAdminUser() {
        Long verificationBodyId = 1L;
        String invitedUserId = "invitedUserId";
        AppUser appUser = AppUser.builder().userId("appUser").build();
        AdminVerifierUserInvitationDTO adminVerifierUserInvitation = AdminVerifierUserInvitationDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .phoneNumber("69999999999")
            .build();

        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();
        String authorityUuid = "uuid";
        
        when(verifierUserAuthService.getUserByEmail(verifierUserInvitation.getEmail()))
			.thenReturn(Optional.empty());
        when(verificationBodyQueryService.existsNonDisabledVerificationBodyById(verificationBodyId)).thenReturn(true);
        when(verifierUserAuthService.registerInvitedVerifierUser(verifierUserInvitation)).thenReturn(invitedUserId);
        when(verifierAuthorityService
            .createPendingAuthority(verificationBodyId, verifierUserInvitation.getRoleCode(), invitedUserId, appUser))
            .thenReturn(authorityUuid);

        verifierUserInvitationService.inviteVerifierAdminUser(appUser, adminVerifierUserInvitation, verificationBodyId);

        // verify
        verify(verifierUserAuthService, times(1)).getUserByEmail(verifierUserInvitation.getEmail());
        verify(verificationBodyQueryService, times(1)).existsNonDisabledVerificationBodyById(verificationBodyId);
        verify(verifierUserAuthService, times(1)).registerInvitedVerifierUser(verifierUserInvitation);
        verify(verifierAuthorityService, times(1))
            .createPendingAuthority(verificationBodyId, verifierUserInvitation.getRoleCode(), invitedUserId, appUser);
        verify(verifierUserNotificationGateway, times(1))
            .notifyInvitedUser(verifierUserInvitation, authorityUuid);
        verifyNoInteractions(verifierUserRegisterValidationService);
    }

    @Test
    void inviteVerifierAdminUser_vb_not_exists() {
        Long verificationBodyId = 1L;
        AppUser appUser = AppUser.builder().userId("appUser").build();
        AdminVerifierUserInvitationDTO adminVerifierUserInvitation = AdminVerifierUserInvitationDTO.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("69999999999")
                .build();

        when(verificationBodyQueryService.existsNonDisabledVerificationBodyById(verificationBodyId)).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                verifierUserInvitationService.inviteVerifierAdminUser(appUser, adminVerifierUserInvitation, verificationBodyId));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(verificationBodyQueryService, times(1)).existsNonDisabledVerificationBodyById(verificationBodyId);
        verify(verifierUserAuthService, never()).registerInvitedVerifierUser(any());
        verify(verifierAuthorityService, never()).createPendingAuthority(anyLong(), anyString(), anyString(), any());
        verify(verifierUserNotificationGateway, never()).notifyInvitedUser(any(), any());
    }

    @Test
    void acceptInvitation_enabled_with_password() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.ACTIVE)
            .userId("userId")
            .build();

        VerifierUserDTO verifierUser = VerifierUserDTO.builder()
            .email(userEmail)
            .enabled(true)
            .build();

		InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
				.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED).build();

        when(verifierUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(userAuthService.hasUserPassword(authorityInfo.getUserId())).thenReturn(true);
        when(verifierUserAuthService.getVerifierUserById(authorityInfo.getUserId())).thenReturn(verifierUser);

        InvitedUserInfoDTO actualInvitedUserInfo = verifierUserInvitationService.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(verifierUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(verifierUserAuthService, times(1)).getVerifierUserById(authorityInfo.getUserId());
        verify(userAuthService, times(1)).hasUserPassword(authorityInfo.getUserId());
        verify(verifierUserActivateService, times(1))
				.acceptAuthorityForRegisteredVerifierInvitedUser(invitationToken);
    }
    
    @Test
    void acceptInvitation_enabled_no_password() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.ACTIVE)
            .userId("userId")
            .build();

        VerifierUserDTO verifierUser = VerifierUserDTO.builder()
            .email(userEmail)
            .enabled(true)
            .build();

		InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
				.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY).build();

        when(verifierUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(userAuthService.hasUserPassword(authorityInfo.getUserId())).thenReturn(false);
        when(verifierUserAuthService.getVerifierUserById(authorityInfo.getUserId())).thenReturn(verifierUser);

        InvitedUserInfoDTO actualInvitedUserInfo = verifierUserInvitationService.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(verifierUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(verifierUserAuthService, times(1)).getVerifierUserById(authorityInfo.getUserId());
        verify(userAuthService, times(1)).hasUserPassword(authorityInfo.getUserId());
        verify(verifierUserActivateService, never())
				.acceptAuthorityForRegisteredVerifierInvitedUser(invitationToken);
    }
    
    @Test
    void acceptInvitation_not_enabled() {
        String invitationToken = "invitationToken";
        String userEmail = "userEmail";
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .authorityStatus(AuthorityStatus.PENDING)
            .userId("userId")
            .build();

        VerifierUserDTO verifierUser = VerifierUserDTO.builder()
            .email(userEmail)
            .enabled(false)
            .build();

		InvitedUserInfoDTO expectedInvitedUserInfo = InvitedUserInfoDTO.builder().email(userEmail)
				.invitationStatus(UserInvitationStatus.PENDING_TO_REGISTERED_SET_PASSWORD_ONLY).build();

        when(verifierUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken)).thenReturn(authorityInfo);
        when(verifierUserAuthService.getVerifierUserById(authorityInfo.getUserId())).thenReturn(verifierUser);

        InvitedUserInfoDTO actualInvitedUserInfo = verifierUserInvitationService.acceptInvitation(invitationToken);

        assertEquals(expectedInvitedUserInfo, actualInvitedUserInfo);

        verify(verifierUserTokenVerificationService, times(1)).verifyInvitationTokenForPendingAuthority(invitationToken);
        verify(verifierUserAuthService, times(1)).getVerifierUserById(authorityInfo.getUserId());
        verifyNoInteractions(verifierUserActivateService);
    }

    private VerifierUserInvitationDTO createVerifierUserInvitationDTO() {
        return VerifierUserInvitationDTO.builder()
            .roleCode("verifier_admin")
            .firstName("firstName")
            .lastName("lastName")
            .email("email")
            .phoneNumber("69999999999")
            .build();
    }
}