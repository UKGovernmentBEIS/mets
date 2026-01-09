package uk.gov.pmrv.api.web.controller.verificationbody;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDeletionService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyUpdateService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.verificationbody.service.VerificationBodyAndUserOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.verificationbody.dto.VerificationBodyCreationDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerificationBodyControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/verification-bodies";

    @InjectMocks
    private VerificationBodyController controller;

    @Mock
    private VerificationBodyAndUserOrchestrator verificationBodyAndUserOrchestrator;
    
    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;
    
    @Mock
    private VerificationBodyUpdateService verificationBodyUpdateService;

    @Mock
    private VerificationBodyDeletionService verificationBodyDeletionService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private Validator validator;

    private MockMvc mockMvc;
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

        controller = (VerificationBodyController) aopProxy.getProxy();
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setValidator(validator)
            .build();
    }

    @Test
    void getVerificationBodies() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        VerificationBodyInfoResponseDTO verificationBodyInfoResponse = VerificationBodyInfoResponseDTO.builder()
                .verificationBodies(List.of(VerificationBodyInfoDTO.builder().id(1L).name("name").status(VerificationBodyStatus.ACTIVE).build(),
                        VerificationBodyInfoDTO.builder().id(2L).name("name2").status(VerificationBodyStatus.PENDING).build()))
                .editable(true)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(verificationBodyQueryService.getVerificationBodies(user)).thenReturn(verificationBodyInfoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("verificationBodies[0].id").value(1L))
                .andExpect(jsonPath("verificationBodies[0].name").value("name"))
                .andExpect(jsonPath("verificationBodies[0].status").value(VerificationBodyStatus.ACTIVE.name()))
                .andExpect(jsonPath("verificationBodies[1].id").value(2L))
                .andExpect(jsonPath("verificationBodies[1].name").value("name2"))
                .andExpect(jsonPath("verificationBodies[1].status").value(VerificationBodyStatus.PENDING.name()));
    }

    @Test
    void getVerificationBodies_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[] {RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(verificationBodyQueryService, never()).getVerificationBodies(any());
    }
    
    @Test
    void getVerificationBodyById() throws Exception {
        Long verificationBodyId = 1L;
        VerificationBodyDTO verificationBodyDTO = VerificationBodyDTO.builder().id(verificationBodyId).name("name").build();
        
        when(verificationBodyQueryService.getVerificationBodyById(verificationBodyId))
            .thenReturn(verificationBodyDTO);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + verificationBodyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(verificationBodyId))
            .andExpect(jsonPath("$.name").value("name"));
    }
    
    @Test
    void getVerificationBodyById_forbidden() throws Exception {
        Long verificationBodyId = 1L;
        AppUser authUser = AppUser.builder().userId("user").build();
        
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(authUser, "getVerificationBodyById");


        mockMvc.perform(
                MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + verificationBodyId))
            .andExpect(status().isForbidden());
        
        verify(verificationBodyQueryService, never()).getVerificationBodyById(Mockito.any());
    }

    @Test
    void deleteVerificationBodyById() throws Exception {
        Long verificationBodyId = 1L;

        mockMvc.perform(MockMvcRequestBuilders
                .delete(CONTROLLER_PATH + "/" + verificationBodyId))
                .andExpect(status().isNoContent());

        verify(verificationBodyDeletionService, times(1)).deleteVerificationBodyById(verificationBodyId);
    }

    @Test
    void deleteVerificationBodyById_forbidden() throws Exception {
        long verificationBodyId = 1L;
        AppUser authUser = AppUser.builder().userId("user").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(authUser, "deleteVerificationBodyById");

        mockMvc.perform(MockMvcRequestBuilders
                .delete(CONTROLLER_PATH + "/" + verificationBodyId))
                .andExpect(status().isForbidden());

        verify(verificationBodyQueryService, never()).getVerificationBodyById(Mockito.any());
    }

    @Test
    void createVerificationBody() throws Exception {
        String vbName = "vbName";
        AppUser appUser = AppUser.builder().userId("authUserId").build();
        VerificationBodyCreationDTO verificationBodyCreationDTO = VerificationBodyCreationDTO.builder()
            .verificationBody(VerificationBodyEditDTO.builder().name(vbName).build())
            .build();

        VerificationBodyInfoDTO verificationBodyInfoDTO = VerificationBodyInfoDTO.builder()
            .id(1L)
            .name(vbName)
            .status(VerificationBodyStatus.PENDING)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(verificationBodyAndUserOrchestrator.createVerificationBody(appUser, verificationBodyCreationDTO))
            .thenReturn(verificationBodyInfoDTO);

        MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationBodyCreationDTO)))
            .andExpect(status().isOk())
            .andReturn();

        VerificationBodyInfoDTO expectedResult =
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), VerificationBodyInfoDTO.class);

        assertEquals(verificationBodyInfoDTO, expectedResult);

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verificationBodyAndUserOrchestrator, times(1))
            .createVerificationBody(appUser, verificationBodyCreationDTO);
    }

    @Test
    void createVerificationBody_forbidden() throws Exception {
        String vbName = "vbName";
        AppUser appUser = AppUser.builder().userId("authUserId").build();
        VerificationBodyCreationDTO verificationBodyCreationDTO = VerificationBodyCreationDTO.builder()
            .verificationBody(VerificationBodyEditDTO.builder().name(vbName).build())
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "createVerificationBody");


        mockMvc.perform(
            MockMvcRequestBuilders.post(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationBodyCreationDTO)))
            .andExpect(status().isForbidden());

        // Verify
        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(verificationBodyAndUserOrchestrator, never()).createVerificationBody(any(), any());
    }
    
    @Test
    void updateVerificationBody() throws Exception {
        Long verificationBodyid = 1L;
        VerificationBodyUpdateDTO verificationBodyUpdateDTO = VerificationBodyUpdateDTO.builder()
                .id(verificationBodyid)
                .verificationBody(VerificationBodyEditDTO.builder().name("name").build())
                .build();
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .put(CONTROLLER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(verificationBodyUpdateDTO)))
                    .andExpect(status().isNoContent());
        
        verify(verificationBodyUpdateService, times(1)).updateVerificationBody(verificationBodyUpdateDTO);
    }
    
    @Test
    void updateVerificationBody_forbidden() throws Exception {
        AppUser authUser = AppUser.builder().userId("user").build();
        Long verificationBodyid = 1L;
        VerificationBodyUpdateDTO verificationBodyUpdateDTO = VerificationBodyUpdateDTO.builder()
                .id(verificationBodyid)
                .verificationBody(VerificationBodyEditDTO.builder().name("name").build())
                .build();
        
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(authUser, "updateVerificationBody");
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .put(CONTROLLER_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(verificationBodyUpdateDTO)))
        .andExpect(status().isForbidden());
        
        verify(verificationBodyUpdateService, never()).updateVerificationBody(Mockito.any());
    }

    @Test
    void updateVerificationBodiesStatus() throws Exception {
        VerificationBodyUpdateStatusDTO verificationBodyUpdateStatusDTO = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();

        mockMvc.perform(MockMvcRequestBuilders
                .patch(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(verificationBodyUpdateStatusDTO))))
                .andExpect(status().isNoContent());

        verify(verificationBodyUpdateService, times(1))
                .updateVerificationBodiesStatus(List.of(verificationBodyUpdateStatusDTO));
    }

    @Test
    void updateVerificationBodiesStatus_forbidden() throws Exception {
        AppUser authUser = AppUser.builder().userId("user").build();
        VerificationBodyUpdateStatusDTO verificationBodyUpdateStatusDTO = VerificationBodyUpdateStatusDTO.builder()
                .id(1L).status(VerificationBodyStatus.ACTIVE).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(authUser, "updateVerificationBodiesStatus");

        mockMvc.perform(MockMvcRequestBuilders
                .patch(CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(verificationBodyUpdateStatusDTO))))
                .andExpect(status().isForbidden());

        verify(verificationBodyUpdateService, never()).updateVerificationBodiesStatus(Mockito.anyList());
    }
}