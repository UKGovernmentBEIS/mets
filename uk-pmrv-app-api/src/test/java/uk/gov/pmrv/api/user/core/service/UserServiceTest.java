package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.user.core.domain.dto.UserDTO;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@InjectMocks
	private UserService cut;

	@Mock
	private AuthService authService;

	@Test
	void getUserByUserId() {
		String userId = "user";
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEmail(userId);
		userRepresentation.setEnabled(true);
		userRepresentation.setFirstName("fn");
		userRepresentation.setLastName("ln");

		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);

		UserDTO result = cut.getUserByUserId(userId);

		assertThat(result)
				.isEqualTo(UserDTO.builder().email(userId).firstName("fn").lastName("ln").enabled(true).build());

		verify(authService, times(1)).getUserRepresentationById(userId);
	}
}
