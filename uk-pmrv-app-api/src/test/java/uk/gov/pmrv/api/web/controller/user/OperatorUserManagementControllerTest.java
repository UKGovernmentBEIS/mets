package uk.gov.pmrv.api.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserManagementService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorUserManagementControllerTest {

    public static final String BASE_PATH = "/v1.0/operator-users";

    private MockMvc mockMvc;

    @InjectMocks
    private OperatorUserManagementController controller;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private OperatorUserManagementService operatorUserManagementService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;
    
    @Mock
    private Validator validator;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (OperatorUserManagementController) aopProxy.getProxy();
    	objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
				.setValidator(validator)
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
	void getOperatorUserById() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(operatorUserManagementService.getOperatorUserByAccountAndId(1L, userId))
				.thenReturn(operatorUserDTO);

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/account/" + 1L + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1)).getOperatorUserByAccountAndId(1L, userId);
	}

	@Test
	void getOperatorUserById_forbidden() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";
		Long accountId = 1L;

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getOperatorUserById", accountId.toString(), null, null);

		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/account/" + accountId + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never()).getOperatorUserByAccountAndId(anyLong(), anyString());
	}

    @Test
	void updateCurrentOperatorUser() throws Exception {
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/operator")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1)).updateOperatorUser(operatorUserDTO);
	}

	@Test
	void updateCurrentOperatorUser_forbidden() throws Exception {
		AppUser user = AppUser.builder().userId("authId").roleType(RoleTypeConstants.REGULATOR).build();
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user, new String[] {RoleTypeConstants.OPERATOR});

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/operator")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never()).updateOperatorUser(any());
	}

	@Test
	void updateOperatorUserById() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		//invoke
		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + 1L + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.firstName").value(operatorUserDTO.getFirstName()))
				.andExpect(jsonPath("$.lastName").value(operatorUserDTO.getLastName()))
				.andExpect(jsonPath("$.email").value(operatorUserDTO.getEmail()));

		verify(operatorUserManagementService, times(1))
				.updateOperatorUserByAccountAndId(1L, userId, operatorUserDTO);
	}

	@Test
	void updateOperatorUserById_forbidden() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";
		OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
				.firstName("fn")
				.lastName("ln")
				.email("email")
				.build();
		Long accountId = 1L;

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateOperatorUserById", accountId.toString(), null, null);

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + accountId + "/" + userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(operatorUserDTO)))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never())
				.updateOperatorUserByAccountAndId( anyLong(), anyString(), any());
	}
	
	@Test
	void resetOperator2Fa() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + 1L + "/" + userId + "/reset-2fa"))
				.andExpect(status().isOk());

		verify(operatorUserManagementService, times(1)).resetOperator2Fa(1L, userId);
	}

	@Test
	void resetOperator2Fa_forbidden() throws Exception {
		AppUser user = AppUser.builder().userId("authId").build();
		String userId = "userId";
		Long accountId = 1L;

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "resetOperator2Fa", accountId.toString(), null, null);

		mockMvc.perform(
				MockMvcRequestBuilders.patch(BASE_PATH + "/account/" + accountId + "/" + userId + "/reset-2fa"))
				.andExpect(status().isForbidden());

		verify(operatorUserManagementService, never()).resetOperator2Fa( anyLong(), anyString());
	}

}
