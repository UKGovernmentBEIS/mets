package uk.gov.pmrv.api.web.controller.user;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.ResetPasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.service.UserResetPasswordService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

@ExtendWith({MockitoExtension.class})
public class ForgotPasswordControllerTest {
	
	private static final String BASE_PATH = "/v1.0/users/forgot-password";
    private static final String SEND_VERIFICATION_EMAIL = "/reset-password-email";
    private static final String TOKEN_VERIFICATION = "/token-verification";
    private static final String RESET_PASSWORD = "/reset-password";
    private static final String EMAIL = "email@email.com";
    private static final String TOKEN = "test.jwt.token";

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;
    
    @Mock
    private UserResetPasswordService userResetPasswordService;
    
    @Mock
    private Validator validator;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(forgotPasswordController)
            .setValidator(validator)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        mapper = new ObjectMapper();
    }

    @Test
    void sendVerificationEmail() throws Exception {
        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH + SEND_VERIFICATION_EMAIL)
						            .contentType(MediaType.APPLICATION_JSON)
						            .content(mapper.writeValueAsString(new EmailDTO(EMAIL))))
            	.andExpect(status().isNoContent());
        
        verify(userResetPasswordService, times(1)).sendResetPasswordEmail(EMAIL);
    }

    @Test
    void verifyToken() throws Exception {
        when(userResetPasswordService.verifyToken(anyString())).thenReturn(EMAIL);

        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH + TOKEN_VERIFICATION)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(new TokenDTO(TOKEN))))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.email").value(EMAIL));
        
        verify(userResetPasswordService, times(1)).verifyToken(TOKEN);
    }

    @Test
    void verifyToken_throwBusinessException() throws Exception {
        when(userResetPasswordService.verifyToken(TOKEN))
        	.thenThrow(new BusinessException(ErrorCode.INVALID_TOKEN));

        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH + TOKEN_VERIFICATION)
				            .contentType(MediaType.APPLICATION_JSON)
				            .content(mapper.writeValueAsString(new TokenDTO(TOKEN))))
            	.andExpect(status().isBadRequest());
        
        verify(userResetPasswordService, times(1)).verifyToken(TOKEN);
    }
    
    @Test
    void resetPassword() throws Exception {
    	ResetPasswordDTO dto = buildMockPasswordDTO();
    	
        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH + RESET_PASSWORD)
						            .contentType(MediaType.APPLICATION_JSON)
						            .content(mapper.writeValueAsString(dto)))
            	.andExpect(status().isNoContent());
        
        verify(userResetPasswordService, times(1)).resetPassword(dto);
    }

	@Test
    void resetPassword_throwBusinessException() throws Exception {
		ResetPasswordDTO dto = buildMockPasswordDTO();
		
		doThrow(new BusinessException(ErrorCode.USER_NOT_EXIST))
	       .when(userResetPasswordService)
	       .resetPassword(Mockito.any(ResetPasswordDTO.class));
    	
        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH + RESET_PASSWORD)
						            .contentType(MediaType.APPLICATION_JSON)
						            .content(mapper.writeValueAsString(dto)))
            	.andExpect(status().isBadRequest());
        
        verify(userResetPasswordService, times(1)).resetPassword(dto);
    }

    private ResetPasswordDTO buildMockPasswordDTO() {
		return ResetPasswordDTO.builder()
    			.token(TOKEN)
    			.otp("123456")
    			.password("Password123!")
    			.build();
	}
}
