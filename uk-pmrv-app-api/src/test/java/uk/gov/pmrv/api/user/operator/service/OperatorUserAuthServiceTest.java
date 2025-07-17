package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserMapper;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserRegistrationMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserAuthServiceTest {

	@InjectMocks
    private OperatorUserAuthService service;

	@Mock
	private AuthService authService;
	
	@Mock
	private UserAuthService userAuthService;

	@Mock
	private OperatorUserMapper operatorUserMapper;

	@Mock
	private OperatorUserRegistrationMapper operatorUserRegistrationMapper;

	@Test
	void getUserIdByEmail() {
		String email = "email";
		String userId = "userId";
		UserInfoDTO userInfo = UserInfoDTO.builder().userId(userId).build();
		
		when(userAuthService.getUserByEmail(email)).thenReturn(Optional.of(userInfo));
		
		Optional<String> result = service.getUserIdByEmail(email);
		
		assertThat(result).isNotEmpty();
		assertThat(result.get()).isEqualTo(userId);
		
		verify(userAuthService, times(1)).getUserByEmail(email);
	}
	
	
	@Test
	void getOperatorUserById() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		String userId = "userId";
		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();
		UserRepresentation userRepresentation = createUserRepresentation(userId, email, "username");

		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);

		//invoke
		OperatorUserDTO result = service.getOperatorUserById(userId);

		assertThat(result).isEqualTo(operatorUserDTO);
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}

	@Test
	void registerAndEnableOperatorUser() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		String password = "password";
		OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO =
				OperatorUserRegistrationWithCredentialsDTO.builder().password(password).build();
		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email(email).firstName(firstName).lastName(lastName).build();

		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setFirstName(firstName);
		userRepresentation.setLastName(lastName);

		// mock
		when(operatorUserRegistrationMapper.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, email))
				.thenReturn(userRepresentation);
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);

		// invoke
		OperatorUserDTO actualUser = service.registerAndEnableOperatorUser(operatorUserRegistrationWithCredentialsDTO, email);

		//assert
		assertThat(actualUser).isEqualTo(operatorUserDTO);

		// verify mocks
		verify(operatorUserRegistrationMapper, times(1)).toUserRepresentation(operatorUserRegistrationWithCredentialsDTO, email);
		verify(authService, times(1)).registerAndEnableUserAndSetPassword(userRepresentation, password);
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}

	@Test
	void registerOperatorUser() {
		String email = "email";
		String firstName = "firstName";
		String lastName = "lastName";
		OperatorUserInvitationDTO operatorUserInvitation = OperatorUserInvitationDTO.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.build();
		
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(email);
		userRepresentation.setFirstName(firstName);
		userRepresentation.setLastName(lastName);

		// mock
		when(operatorUserMapper.toUserRepresentation(operatorUserInvitation)).thenReturn(userRepresentation);
		when(authService.saveUser(userRepresentation)).thenReturn("user");
		// invoke
		String actualUserId = service.registerOperatorUser(operatorUserInvitation);

		//assert
		assertThat(actualUserId).isEqualTo("user");

		// verify mocks
		verify(operatorUserMapper, times(1)).toUserRepresentation(operatorUserInvitation);
		verify(authService, times(1)).saveUser(userRepresentation);
	}
	
	@Test
    void updateUser_οperatorUserInvitationDTO() {
        String email = "email";
        String firstName = "firstName";
        String lastName = "lastName";
        OperatorUserInvitationDTO operatorUserInvitation = OperatorUserInvitationDTO.builder()
            .email(email)
            .firstName(firstName)
            .lastName(lastName)
            .build();
        UserRepresentation userRepresentation = new UserRepresentation();

        when(operatorUserMapper.toUserRepresentation(operatorUserInvitation)).thenReturn(userRepresentation);

        service.updateUser(operatorUserInvitation);

        verify(operatorUserMapper, times(1)).toUserRepresentation(operatorUserInvitation);
        verify(authService, times(1)).saveUser(userRepresentation);
    }

	@Test
	void updateUser_οperatorUserDTO() {
		String userId = "user";
		String username = "username";
		UserRepresentation userRepresentationUpdated = createUserRepresentation(userId, "email2", username);

		OperatorUserDTO operatorUserDTO =
				OperatorUserDTO.builder().email("email2").firstName("fn").lastName("ln").build();

		when(operatorUserMapper.toUserRepresentation(operatorUserDTO)).thenReturn(userRepresentationUpdated);

		//invoke
		service.updateUser(operatorUserDTO);

		verify(operatorUserMapper, times(1)).toUserRepresentation(operatorUserDTO);
		verify(authService, times(1)).saveUser(userRepresentationUpdated);
	}
	
	@Test
	void enableAndUpdateUserAndSetPassword() {
		OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO = OperatorUserRegistrationWithCredentialsDTO
				.builder().emailToken("emailtoken").password("pass").build();
		
		String userId = "userId";
		
		UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", "email1");

		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder().email("email1").build();
		
		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserRegistrationMapper.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO,
				userRepresentation.getEmail()))
				.thenReturn(userRepresentation);
		
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);
		
		OperatorUserDTO result = service.enableAndUpdateUserAndSetPassword(operatorUserRegistrationWithCredentialsDTO, userId);
		
		assertThat(result).isEqualTo(operatorUserDTO);
		
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(authService, times(1)).enableAndSaveUserAndSetPassword(userRepresentation, "pass");
		verify(operatorUserRegistrationMapper, times(1)).toUserRepresentation(operatorUserRegistrationWithCredentialsDTO,
				userRepresentation.getEmail());
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}
	
	@Test
	void enableAndUpdateUser() {
		OperatorUserRegistrationDTO operatorUserRegistrationWithCredentialsDTO = OperatorUserRegistrationDTO
				.builder().emailToken("emailtoken").build();
		
		String userId = "userId";
		
		UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", "email1");

		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder().email("email1").build();
		
		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(operatorUserRegistrationMapper.toUserRepresentation(operatorUserRegistrationWithCredentialsDTO,
				userRepresentation.getEmail()))
				.thenReturn(userRepresentation);
		
		when(operatorUserMapper.toOperatorUserDTO(userRepresentation)).thenReturn(operatorUserDTO);
		
		OperatorUserDTO result = service.enableAndUpdateUser(operatorUserRegistrationWithCredentialsDTO, userId);
		
		assertThat(result).isEqualTo(operatorUserDTO);
		
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(authService, times(1)).enableAndSaveUser(userRepresentation);
		verify(operatorUserRegistrationMapper, times(1)).toUserRepresentation(operatorUserRegistrationWithCredentialsDTO,
				userRepresentation.getEmail());
		verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
	}
	
    @Test
    void setUserPassword() {
        String userId = "userId";
        String email = "email";
        String password = "password";

        UserRepresentation userRepresentation = createUserRepresentation(userId, email, email);

        when(authService.setUserPassword(userId, password)).thenReturn(userRepresentation);

	    service.setUserPassword(userId, password);

	    verify(authService, times(1)).setUserPassword(userId, password);
	    verify(operatorUserMapper, times(1)).toOperatorUserDTO(userRepresentation);
    }

	private UserRepresentation createUserRepresentation(String id, String email, String username) {
		UserRepresentation user = new UserRepresentation();
		user.setId(id);
		user.setEmail(email);
		user.setUsername(username);
		return user;
	}

}
