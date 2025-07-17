package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserManagementServiceTest {

	@InjectMocks
    private VerifierUserManagementService service;
	
	@Mock
	private VerifierAuthorityService verifierAuthorityService;
	
	@Mock
	private VerifierUserAuthService verifierUserAuthService;
	
	@Mock
	private UserSecuritySetupService userSecuritySetupService;
	
	@Test
	void getVerifierUserById_verifier_auth_user() {
		final String userId = "userId";
		final Long verBodyId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.VERIFIER)
            .authorities(List.of(AppAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(true);

		// Invoke
		service.getVerifierUserById(appUser, userId);

		// Assert
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
		verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void getVerifierUserById_regulator_auth_user() {
        final String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(true);

        // Invoke
        service.getVerifierUserById(appUser, userId);

        // Assert
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verify(verifierUserAuthService, times(1)).getVerifierUserById(userId);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

    @Test
    void getVerifierUserById_auth_user_role_not_supported() {
        final String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.OPERATOR)
            .build();

        assertThrows(UnsupportedOperationException.class, () -> service.getVerifierUserById(appUser, userId));

        verifyNoInteractions(verifierUserAuthService);
        verifyNoInteractions(verifierAuthorityService);
    }

	@Test
	void getVerifierUserById_verifier_auth_user_wanted_user_not_verifier() {
        final String userId = "userId";
        final Long verBodyId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.VERIFIER)
            .authorities(List.of(AppAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getVerifierUserById(appUser, userId));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, businessException.getErrorCode());
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void getVerifierUserById_regulator_auth_user_wanted_user_not_verifier() {
        final String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.getVerifierUserById(appUser, userId));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER, businessException.getErrorCode());
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

	@Test
	void updateVerifierUserById_verifier_auth_user() {
		String userId = "userId";
		final Long verBodyId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.VERIFIER)
            .authorities(List.of(AppAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();
		VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(true);

		// Invoke
		service.updateVerifierUserById(appUser, userId, verifierUserDTO);

		// Assert
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verify(verifierUserAuthService, times(1)).updateVerifierUser(verifierUserDTO);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void updateVerifierUserById_regulator_auth_user() {
        String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.REGULATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(true);

        // Invoke
        service.updateVerifierUserById(appUser, userId, verifierUserDTO);

        // Assert
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verify(verifierUserAuthService, times(1)).updateVerifierUser(verifierUserDTO);
        verifyNoMoreInteractions(verifierAuthorityService);
    }

    @Test
    void updateVerifierUserById_auth_user_role_not_supported() {
        String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.OPERATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        assertThrows(UnsupportedOperationException.class,
            () -> service.updateVerifierUserById(appUser, userId, verifierUserDTO));

        verifyNoInteractions(verifierUserAuthService);
        verifyNoInteractions(verifierAuthorityService);
    }

	@Test
	void updateVerifierUserById_verifier_auth_user_wanted_user_not_verifier() {
		String userId = "userId";
		final Long verBodyId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.VERIFIER)
            .authorities(List.of(AppAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Mock
		when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateVerifierUserById(appUser, userId, verifierUserDTO));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, businessException.getErrorCode());
		verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
		verifyNoInteractions(verifierUserAuthService);
        verifyNoMoreInteractions(verifierAuthorityService);
	}

    @Test
    void updateVerifierUserById_regulator_auth_user_wanted_user_not_verifier() {
        String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.REGULATOR)
            .build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.updateVerifierUserById(appUser, userId, verifierUserDTO));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER, businessException.getErrorCode());
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verifyNoMoreInteractions(verifierAuthorityService);
        verifyNoInteractions(verifierUserAuthService);
    }

	@Test
	void updateCurrentVerifierUser() {
		VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().build();

		// Invoke
		service.updateCurrentVerifierUser(verifierUserDTO);

		// Assert
		verify(verifierUserAuthService, times(1)).updateVerifierUser(verifierUserDTO);
	}
	
	@Test
    void resetVerifier2Fa() {
        String userId = "userId";
        final Long verBodyId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.VERIFIER)
            .authorities(List.of(AppAuthority.builder().verificationBodyId(verBodyId).build()))
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyId(userId, verBodyId)).thenReturn(true);

        // Invoke
        service.resetVerifier2Fa(appUser, userId);

        // Assert
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyId(userId, verBodyId);
        verify(userSecuritySetupService, times(1)).resetUser2Fa(userId);
    }

    @Test
    void resetVerifier2Fa_user_not_verifier() {
        String userId = "userId";
        AppUser appUser = AppUser.builder()
            .userId("authUserId")
            .roleType(RoleTypeConstants.REGULATOR)
            .build();

        // Mock
        when(verifierAuthorityService.existsByUserIdAndVerificationBodyIdNotNull(userId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.resetVerifier2Fa(appUser, userId));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER, businessException.getErrorCode());
        verify(verifierAuthorityService, times(1)).existsByUserIdAndVerificationBodyIdNotNull(userId);
        verify(userSecuritySetupService, never()).resetUser2Fa(anyString());
    }
	
}
