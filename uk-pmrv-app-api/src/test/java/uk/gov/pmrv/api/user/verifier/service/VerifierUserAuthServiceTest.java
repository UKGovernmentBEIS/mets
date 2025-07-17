package uk.gov.pmrv.api.user.verifier.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.transform.VerifierUserMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserAuthServiceTest {

    @InjectMocks
    private VerifierUserAuthService service;

    @Mock
    private AuthService authService;
    
    @Mock
    private UserAuthService userAuthService;

    @Mock
    private VerifierUserMapper verifierUserMapper;
    
    @Test
    void getUserByEmail() {
    	String email = "email";
    	UserInfoDTO user = UserInfoDTO.builder().email(email).build();
    	
    	when(userAuthService.getUserByEmail(email)).thenReturn(Optional.of(user));
    	
    	Optional<UserInfoDTO> result = service.getUserByEmail(email);
    	assertThat(result).isNotEmpty();
    	assertThat(result.get()).isEqualTo(user);
    	
    	verify(userAuthService, times(1)).getUserByEmail(email);
    }

    @Test
    void getVerifierUserById() {
        String userId = "userId";
        UserRepresentation userRepresentation = createUserRepresentation(userId, "email");
        VerifierUserDTO expected = createVerifierUserDTO("email");

        // Mock
        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(verifierUserMapper.toVerifierUserDTO(userRepresentation)).thenReturn(expected);

        // Invoke
        VerifierUserDTO actual = service.getVerifierUserById(userId);

        // Assert
        assertEquals(expected, actual);
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(verifierUserMapper, times(1)).toVerifierUserDTO(userRepresentation);
    }

    @Test
    void updateVerifierUser() {
        UserRepresentation userRepresentationUpdated = createUserRepresentation("userId", "email2");
        VerifierUserDTO verifierUserDTO = createVerifierUserDTO("email");

        // Mock
        when(verifierUserMapper.toUserRepresentation(verifierUserDTO)).thenReturn(userRepresentationUpdated);

        // Invoke
        service.updateVerifierUser(verifierUserDTO);

        // Assert
        verify(verifierUserMapper, times(1)).toUserRepresentation(verifierUserDTO);
        verify(authService, times(1)).saveUser(userRepresentationUpdated);
    }

    @Test
    void registerInvitedVerifierUser() {
        final String userId = "user";
        VerifierUserInvitationDTO verifierUserInvitation = VerifierUserInvitationDTO.builder().email("email").build();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        when(verifierUserMapper.toUserRepresentation(verifierUserInvitation)).thenReturn(userRepresentation);
        when(authService.saveUser(userRepresentation)).thenReturn(userId);

        String actualUserId = service.registerInvitedVerifierUser(verifierUserInvitation);

        assertThat(actualUserId).isEqualTo(userId);

        verify(verifierUserMapper, times(1)).toUserRepresentation(verifierUserInvitation);
        verify(authService, times(1)).saveUser(userRepresentation);
    }

    private UserRepresentation createUserRepresentation(String id, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setId(id);
        user.setEmail(email);
        user.setUsername(email);
        return user;
    }

    private VerifierUserDTO createVerifierUserDTO(String email) {
        return VerifierUserDTO.builder()
                .email(email)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("2101313131")
                .enabled(true)
                .build();
    }
}
