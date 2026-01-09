package uk.gov.pmrv.api.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserActivateService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifierUserRegistrationControllerTest {

    private static final String BASE_PATH = "/v1.0/verifier-users/registration";
    private static final String ACCEPT_INVITATION_PATH = "/accept-invitation";
    private static final String ACCEPT_AUTH_AND_ACTIVATE_USER_FROM_INVITATION = "/accept-authority-and-activate-verifier-user-from-invitation";

    private MockMvc mockMvc;

    @InjectMocks
    private VerifierUserRegistrationController controller;

    @Mock
    private VerifierUserInvitationService verifierUserInvitationService;

    @Mock
    private VerifierUserActivateService verifierUserActivateService;

    @Mock
    private Validator validator;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setValidator(validator)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void acceptVerifierInvitation() throws Exception {
        String email = "email";
        TokenDTO tokenDTO = TokenDTO.builder().token("token").build();
        InvitedUserInfoDTO invitedUserInfo = InvitedUserInfoDTO.builder().email(email).build();

        // Mock
        when(verifierUserInvitationService.acceptInvitation(tokenDTO.getToken())).thenReturn(invitedUserInfo);

        // Invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + ACCEPT_INVITATION_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void acceptAuthorityAndActivateUserFromInvite() throws Exception {
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();

        // Invoke
        mockMvc.perform(
            MockMvcRequestBuilders.put(BASE_PATH + ACCEPT_AUTH_AND_ACTIVATE_USER_FROM_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invitedUserCredentialsDTO)))
            .andExpect(status().isNoContent());

        verify(verifierUserActivateService, times(1)).acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);
    }
    
}
