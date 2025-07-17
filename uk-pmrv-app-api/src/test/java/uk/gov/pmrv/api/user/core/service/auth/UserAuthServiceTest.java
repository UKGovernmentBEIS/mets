package uk.gov.pmrv.api.user.core.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.transform.UserMapper;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthServiceTest {

	@InjectMocks
    private UserAuthService service;
	
	@Mock
	private AuthService authService;
	
	@Mock
	private UserMapper userMapper;
	
	@Test
	void getUserByEmail() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setFirstName(firstName);
		userRepresentation.setLastName(lastName);
		
		UserInfoDTO userDTO = UserInfoDTO.builder().firstName(firstName).lastName(lastName).email(email).build();
		
		when(authService.getByEmail(email)).thenReturn(Optional.of(userRepresentation));
		when(userMapper.toUserInfoDTO(userRepresentation)).thenReturn(userDTO);
		
		//invoke
		Optional<UserInfoDTO> userInfoActualOptional = service.getUserByEmail(email);
		
		assertThat(userInfoActualOptional).contains(userDTO);
		
		verify(authService, times(1)).getByEmail(email);
		verify(userMapper, times(1)).toUserInfoDTO(userRepresentation);
	}
	
	@Test
	void getUserByEmail_not_found() {
		String email = "email";
		
		when(authService.getByEmail(email)).thenReturn(Optional.empty());
		
		//invoke
		Optional<UserInfoDTO> userInfoActualOptional = service.getUserByEmail(email);
		
		assertThat(userInfoActualOptional).isNotPresent();
		
		verify(authService, times(1)).getByEmail(email);
		verifyNoInteractions(userMapper);
	}

	@Test
	void deleteOtpCredentials() {
		String email = "email";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId(email);

		when(authService.getByEmail(email)).thenReturn(Optional.of(userRepresentation));

		service.deleteOtpCredentialsByEmail(email);

		verify(authService, times(1)).deleteOtpCredentials(userRepresentation.getId());
		verify(authService, times(1)).deleteUserSessions(userRepresentation.getId());
	}

	@Test
	void deleteOtpCredentials_user_not_exist() {
		String email = "email";

		when(authService.getByEmail(email)).thenReturn(Optional.empty());

		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.deleteOtpCredentialsByEmail(email));

		assertEquals(ErrorCode.USER_NOT_EXIST, businessException.getErrorCode());
		verify(authService, never()).deleteOtpCredentials(anyString());
	}
	
	@Test
	void getUserDetails() {
	    String userId = "userId";
        UserDetails userDetails = UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("sign").build())
                .build();
        
        when(authService.getUserDetails(userId)).thenReturn(Optional.of(userDetails));
        
        Optional<UserDetails> result = service.getUserDetails(userId);
        
        assertThat(result.get()).isEqualTo(userDetails);
        verify(authService, times(1)).getUserDetails(userId);
	}
	
	@Test
    void getUserSignature() {
	    String signatureUuid = UUID.randomUUID().toString();
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
        
        when(authService.getUserSignature(signatureUuid)).thenReturn(Optional.of(signature));
        
        Optional<FileDTO> result = service.getUserSignature(signatureUuid);
        
        assertThat(result.get()).isEqualTo(signature);
        verify(authService, times(1)).getUserSignature(signatureUuid);
    }
	
	@Test
	void resetPassword() {
		String email = "email";
		String otp = "otp";
		String password = "password";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setId(email);

		when(authService.getByEmail(email)).thenReturn(Optional.of(userRepresentation));

		service.resetPassword(email, otp, password);

		verify(authService, times(1)).resetUserPassword(userRepresentation, password, otp);
		verify(authService, times(1)).deleteUserSessions(userRepresentation.getId());
	}

	@Test
	void resetPassword_user_not_exist() {
		String email = "email";

		when(authService.getByEmail(email)).thenReturn(Optional.empty());

		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.resetPassword(email, null, null));

		assertEquals(ErrorCode.USER_NOT_EXIST, businessException.getErrorCode());
		verify(authService, times(0)).resetUserPassword(any(), anyString(), anyString());
		verify(authService, times(0)).deleteUserSessions(anyString());
	}
	
	@Test
	void setUserPassword() {
		String userId = "userId";
		String password = "pass";
		
		service.setUserPassword(userId, password);
		
		verify(authService, times(1)).setUserPassword(userId, password);
	}
}
