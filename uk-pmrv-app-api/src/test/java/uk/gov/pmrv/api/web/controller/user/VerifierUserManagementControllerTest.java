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
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserManagementService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifierUserManagementControllerTest {

	public static final String BASE_PATH = "/v1.0/verifier-users";

    private MockMvc mockMvc;

    @InjectMocks
    private VerifierUserManagementController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private VerifierUserManagementService verifierUserManagementService;
    
    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private Validator validator;

    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (VerifierUserManagementController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
				.setValidator(validator)
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }
    
    @Test
    void getVerifierUserById() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(verifierUserManagementService.getVerifierUserById(user, userId)).thenReturn(verifierUserDTO);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).getVerifierUserById(user, userId);
    }

    @Test
    void getVerifierUserById_forbidden() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("currentuser").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getVerifierUserById");

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.get(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).getVerifierUserById(any(), anyString());
    }

    @Test
    void updateVerifierUserById() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).updateVerifierUserById(user, userId, verifierUserDTO);
    }

    @Test
    void updateVerifierUserById_forbidden() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateVerifierUserById");

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isForbidden());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).updateVerifierUserById(any(), anyString(), any());
    }

    @Test
    void updateCurrentVerifierUser() throws Exception {
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(verifierUserDTO.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(verifierUserDTO.getLastName()))
                .andExpect(jsonPath("$.email").value(verifierUserDTO.getEmail()));

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).updateCurrentVerifierUser(verifierUserDTO);
    }

    @Test
    void updateCurrentVerifierUser_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("currentuser").build();
        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder().firstName("fName")
                .lastName("lName").email("email").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[] {RoleTypeConstants.VERIFIER});

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/verifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifierUserDTO)))
                .andExpect(status().isForbidden());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).updateCurrentVerifierUser(any());
    }
    
    @Test
    void resetVerifier2Fa() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("authId").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId + "/reset-2fa"))
                .andExpect(status().isOk());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, times(1)).resetVerifier2Fa(user, userId);
    }

    @Test
    void resetVerifier2Fa_forbidden() throws Exception {
        final String userId = "userId";
        AppUser user = AppUser.builder().userId("authId").build();

        // Mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "resetVerifier2Fa");

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders.patch(BASE_PATH + "/" + userId + "/reset-2fa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verifierUserManagementService, never()).resetVerifier2Fa(any(), anyString());
    }
}
