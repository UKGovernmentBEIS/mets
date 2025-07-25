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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationResult;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationStatus;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAcceptInvitationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserActivationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserRegistrationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegistrationControllerTest {

	private static final String USER_CONTROLLER_PATH = "/v1.0/operator-users/registration";
	private static final String SEND_VERIFICATION_EMAIL_PATH = "/verification-email";
	private static final String VERIFY_TOKEN_PATH = "/token-verification";
	private static final String REGISTER_PATH = "/register";
	private static final String ACCEPT_INVITATION_PATH = "/accept-invitation";
	private static final String ENABLE_WITH_CREDENTIALS_FROM_INVITATION = "/accept-authority-and-enable-invited-operator-with-credentials";
	private static final String ENABLE_NO_CREDENTIALS_FROM_INVITATION = "/accept-authority-and-enable-invited-operator-without-credentials";
	private static final String ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER = "/accept-authority-and-set-credentials-to-user";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorUserRegistrationController controller;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;
    
    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;

    @Mock
    private OperatorUserRegistrationService operatorUserRegistrationService;

	@Mock
	private OperatorUserActivationService operatorUserActivationService;
    
    @Mock
    private OperatorUserAcceptInvitationService operatorUserAcceptInvitationService;
    
    @Mock
    private Validator validator;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setValidator(validator)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void sendVerificationEmail() throws Exception {
    	final String email = "email";
        mockMvc.perform(
        		MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + SEND_VERIFICATION_EMAIL_PATH)
						            .contentType(MediaType.APPLICATION_JSON)
						            .content(mapper.writeValueAsString(new EmailDTO(email))))
            	.andExpect(status().isNoContent());
        
        verify(operatorUserRegistrationService, times(1)).sendVerificationEmail(email);
    }

    @Test
    void verifyUserRegistrationToken() throws Exception {
    	final String email = "email";
    	final String token = "token";
        when(operatorUserTokenVerificationService.verifyRegistrationTokenAndResolveAndValidateUserExistence(anyString())).thenReturn(OperatorUserTokenVerificationResult.builder()
        		.email(email).status(OperatorUserTokenVerificationStatus.NOT_REGISTERED)
        		.build());

        mockMvc.perform(
        		MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + VERIFY_TOKEN_PATH)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(new TokenDTO(token))))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.email").value(email))
	            .andExpect(jsonPath("$.status").value(OperatorUserTokenVerificationStatus.NOT_REGISTERED.name()));
        
        verify(operatorUserTokenVerificationService, times(1)).verifyRegistrationTokenAndResolveAndValidateUserExistence(token);
    }

    @Test
    void verifyUserRegistrationToken_throwBusinessException() throws Exception {
    	final String token = "token";
        when(operatorUserTokenVerificationService.verifyRegistrationTokenAndResolveAndValidateUserExistence(token))
        	.thenThrow(new BusinessException(ErrorCode.USER_ROLE_ALREADY_EXISTS));

        mockMvc.perform(
        		MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + VERIFY_TOKEN_PATH)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(new TokenDTO(token))))
            	.andExpect(status().isBadRequest());
        
        verify(operatorUserTokenVerificationService, times(1)).verifyRegistrationTokenAndResolveAndValidateUserExistence(token);
    }
    
    @Test
    void registerUser() throws Exception {
    	final String email = "email";
    	final String token = "token";
    	OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO =
    			OperatorUserRegistrationWithCredentialsDTO.builder().emailToken(token).firstName("fn").lastName("ln").build();
    	OperatorUserDTO userDTO = 
    			OperatorUserDTO.builder().email(email).firstName("fn").lastName("ln").build();
    	
        when(operatorUserRegistrationService.registerUser(userRegistrationDTO)).thenReturn(userDTO);

        mockMvc.perform(
        		MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + REGISTER_PATH)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(userRegistrationDTO)))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.email").value(email));
        
        verify(operatorUserRegistrationService, times(1)).registerUser(userRegistrationDTO);
    }
    
    @Test
    void registerUser_throw_internal_server_error() throws Exception {
    	final String token = "token";
    	OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO =
    			OperatorUserRegistrationWithCredentialsDTO.builder().emailToken(token).firstName("fn").lastName("ln").build();
    	
        when(operatorUserRegistrationService.registerUser(userRegistrationDTO))
        	.thenThrow(new BusinessException(ErrorCode.USER_REGISTRATION_FAILED_500));

        mockMvc.perform(
        		MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + REGISTER_PATH)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(userRegistrationDTO)))
            	.andExpect(status().isInternalServerError());
        
        verify(operatorUserRegistrationService, times(1)).registerUser(userRegistrationDTO);
    }
    
    @Test
    void acceptInvitation() throws Exception {
    	TokenDTO tokenDTO = new TokenDTO();
    	tokenDTO.setToken("token");

        OperatorInvitedUserInfoDTO operatorInvitedUserInfo = OperatorInvitedUserInfoDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .roleCode("code")
            .invitationStatus(UserInvitationStatus.ACCEPTED)
            .build();

    	when(operatorUserAcceptInvitationService.acceptInvitation(tokenDTO.getToken()))
    		.thenReturn(operatorInvitedUserInfo);

        mockMvc.perform(MockMvcRequestBuilders.post(USER_CONTROLLER_PATH + ACCEPT_INVITATION_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(tokenDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(operatorInvitedUserInfo.getEmail()))
            .andExpect(jsonPath("$.firstName").value(operatorInvitedUserInfo.getFirstName()))
            .andExpect(jsonPath("$.lastName").value(operatorInvitedUserInfo.getLastName()))
            .andExpect(jsonPath("$.roleCode").value(operatorInvitedUserInfo.getRoleCode()))
            .andExpect(jsonPath("$.invitationStatus").value(operatorInvitedUserInfo.getInvitationStatus().name()));

        verify(operatorUserAcceptInvitationService, times(1)).acceptInvitation(tokenDTO.getToken());
    }

    @Test
    void acceptAuthorityAndEnableInvitedUserWithCredentials() throws Exception {
        OperatorUserRegistrationWithCredentialsDTO user = OperatorUserRegistrationWithCredentialsDTO.builder()
                .emailToken("token").firstName("fn").lastName("ln").build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email").firstName("fn").lastName("ln").build();

        when(operatorUserActivationService.acceptAuthorityAndEnableInvitedUserWithCredentials(user)).thenReturn(userDTO);

        mockMvc.perform(
                MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ENABLE_WITH_CREDENTIALS_FROM_INVITATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("fn"))
                .andExpect(jsonPath("$.lastName").value("ln"))
                .andExpect(jsonPath("$.email").value("email"));

        verify(operatorUserActivationService, times(1)).acceptAuthorityAndEnableInvitedUserWithCredentials(user);
    }

    @Test
    void acceptAuthorityAndEnableInvitedUser() throws Exception {
        OperatorUserRegistrationDTO user = OperatorUserRegistrationDTO.builder()
            .emailToken("token").firstName("fn").lastName("ln").build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email").firstName("fn").lastName("ln").build();

        when(operatorUserActivationService.acceptAuthorityAndEnableInvitedUser(user)).thenReturn(userDTO);

        mockMvc.perform(
            MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ENABLE_NO_CREDENTIALS_FROM_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("fn"))
            .andExpect(jsonPath("$.lastName").value("ln"))
            .andExpect(jsonPath("$.email").value("email"));

        verify(operatorUserActivationService, times(1)).acceptAuthorityAndEnableInvitedUser(user);
    }
    
    @Test
    void acceptAuthorityAndSetCredentialsToUser() throws Exception {
        InvitedUserCredentialsDTO operatorUser = InvitedUserCredentialsDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();

        mockMvc.perform(
            MockMvcRequestBuilders.put(USER_CONTROLLER_PATH + ACCEPT_AUTHORITY_AND_SET_CREDENTIALS_TO_USER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(operatorUser)))
            .andExpect(status().isNoContent());

        verify(operatorUserActivationService, times(1)).acceptAuthorityAndSetCredentialsToUser(operatorUser);
    }
}