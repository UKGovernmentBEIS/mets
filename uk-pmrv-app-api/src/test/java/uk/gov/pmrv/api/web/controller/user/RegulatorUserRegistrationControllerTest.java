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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserActivateService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegulatorUserRegistrationControllerTest {

    private static final String BASE_PATH = "/v1.0/regulator-users/registration";
    private static final String ACCEPT_INVITATION_PATH = "/accept-invitation";
    private static final String ACCEPT_AUTH_AND_ACTIVATE_USER_FROM_INVITATION = "/accept-authority-and-activate-user-from-invitation";

    @InjectMocks
    private RegulatorUserRegistrationController regulatorUserRegistrationController;

    @Mock
    private RegulatorUserInvitationService regulatorUserInvitationService;

    @Mock
    private RegulatorUserActivateService regulatorUserActivateService;

    @Mock
    private Validator validator;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(regulatorUserRegistrationController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .build();
    }

    @Test
    void acceptInvitation() throws Exception {
        String email = "email";
        TokenDTO invitationToken = TokenDTO.builder().token("token").build();
        InvitedUserInfoDTO invitedUserInfo = InvitedUserInfoDTO.builder().email(email).build();

        when(regulatorUserInvitationService.acceptInvitation(invitationToken.getToken()))
            .thenReturn(invitedUserInfo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + ACCEPT_INVITATION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invitationToken)))
            .andExpect(status().isOk())
            .andReturn();

        InvitedUserInfoDTO actualResult =
            objectMapper.readValue(result.getResponse().getContentAsString(), InvitedUserInfoDTO.class);

        assertEquals(invitedUserInfo, actualResult);

        verify(regulatorUserInvitationService, times(1)).acceptInvitation(invitationToken.getToken());
    }

    @Test
    void acceptInvitation_bad_request_exception() throws Exception {
        TokenDTO invitationToken = new TokenDTO();
        invitationToken.setToken("token");

        doThrow(new BusinessException(ErrorCode.USER_INVALID_STATUS)).when(regulatorUserInvitationService)
            .acceptInvitation(invitationToken.getToken());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + ACCEPT_INVITATION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invitationToken)))
            .andExpect(status().isBadRequest());

        verify(regulatorUserInvitationService, times(1)).acceptInvitation(invitationToken.getToken());
    }

    @Test
    void acceptAuthorityAndActivateUserFromInvite() throws Exception{
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("invitationToken")
            .password("password")
            .build();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + ACCEPT_AUTH_AND_ACTIVATE_USER_FROM_INVITATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invitedUserCredentialsDTO)))
            .andExpect(status().isNoContent());

        verify(regulatorUserActivateService, times(1)).acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);
    }

    @Test
    void acceptAuthorityAndActivateUserFromInvite_bad_request_exception() throws Exception{
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("invitationToken")
            .password("password")
            .build();

        doThrow(new BusinessException(ErrorCode.INVALID_TOKEN)).when(regulatorUserActivateService)
            .acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH + ACCEPT_AUTH_AND_ACTIVATE_USER_FROM_INVITATION)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invitedUserCredentialsDTO)))
            .andExpect(status().isBadRequest());

        verify(regulatorUserActivateService, times(1)).acceptAuthorityAndActivateInvitedUser(invitedUserCredentialsDTO);
    }
    
}