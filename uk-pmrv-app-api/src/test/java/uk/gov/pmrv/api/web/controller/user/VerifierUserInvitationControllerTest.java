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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifierUserInvitationControllerTest {

    public static final String BASE_PATH = "/v1.0/verifier-users/invite";

    @InjectMocks
    private VerifierUserInvitationController controller;

    @Mock
    private VerifierUserInvitationService verifierUserInvitationService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (VerifierUserInvitationController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        Validator validator = mock(Validator.class);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .build();


    }

    @Test
    void inviteVerifierUser() throws Exception {
        AppUser appUser = AppUser.builder().userId("appUser").build();

        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifierUserInvitation))
        )
            .andExpect(status().isNoContent());

        verify(verifierUserInvitationService, times(1)).inviteVerifierUser(appUser, verifierUserInvitation);
    }

    @Test
    void inviteVerifierUser_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("appUser").build();

        VerifierUserInvitationDTO verifierUserInvitation = createVerifierUserInvitationDTO();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "inviteVerifierUser");

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifierUserInvitation))
        )
            .andExpect(status().isForbidden());

        verify(verifierUserInvitationService, never()).inviteVerifierUser(any(), any());
    }

    @Test
    void inviteVerifierAdminUserByVerificationBodyId() throws Exception {
        final Long vbId = 1L;
        AppUser appUser = AppUser.builder().userId("appUser").build();
        AdminVerifierUserInvitationDTO adminVerifierUserInvitation = createAdminVerifierUserInvitationDTO();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/vb/" + vbId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminVerifierUserInvitation)))
                .andExpect(status().isNoContent());

        verify(verifierUserInvitationService, times(1))
                .inviteVerifierAdminUser(appUser, adminVerifierUserInvitation, vbId);
    }

    @Test
    void inviteVerifierAdminUserByVerificationBodyId_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("appUser").build();
        AdminVerifierUserInvitationDTO adminVerifierUserInvitation = createAdminVerifierUserInvitationDTO();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "inviteVerifierAdminUserByVerificationBodyId");

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "/vb/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminVerifierUserInvitation)))
                .andExpect(status().isForbidden());

        verify(verifierUserInvitationService, never()).inviteVerifierAdminUser(any(), any(), anyLong());
    }

    private VerifierUserInvitationDTO createVerifierUserInvitationDTO() {
        return VerifierUserInvitationDTO.builder()
            .roleCode("verifier")
            .firstName("firstName")
            .lastName("lastName")
            .email("email@keycloak.gr")
            .phoneNumber("69999999999")
            .build();
    }

    private AdminVerifierUserInvitationDTO createAdminVerifierUserInvitationDTO() {
        return AdminVerifierUserInvitationDTO.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("69999999999")
                .build();
    }
}