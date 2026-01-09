package uk.gov.pmrv.api.web.controller.account;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.account.domain.dto.AppointVerificationBodyDTO;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyAppointService;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyService;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/accounts";
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private AccountVerificationBodyController controller;
    
    @Mock
    private AccountVerificationBodyService accountVerificationBodyService;
    
    @Mock
    private AccountVerificationBodyAppointService accountVerificationBodyAppointService;
    
    @Mock
    private AppSecurityComponent pmrvSecurityComponent;
    
    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AccountVerificationBodyController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
    }
    
    @Test
    void getVerificationBodyOfAccount() throws Exception {
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        String verificationBodyName = "vb";
        VerificationBodyNameInfoDTO vb = VerificationBodyNameInfoDTO.builder().id(verificationBodyId).name(verificationBodyName).build();
        
        when(accountVerificationBodyService.getVerificationBodyNameInfoByAccount(accountId))
            .thenReturn(Optional.of(vb));
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId + "/verification-body")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(verificationBodyId))
            .andExpect(jsonPath("$.name").value(verificationBodyName));

        verify(accountVerificationBodyService, times(1)).getVerificationBodyNameInfoByAccount(accountId);
    }
    
    @Test
    void getVerificationBodyOfAccount_forbidden() throws Exception {
        Long accountId = 1L;
        AppUser user = AppUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getVerificationBodyOfAccount", Long.toString(accountId), null, null);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + "/" + accountId + "/verification-body")
                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isForbidden());

        verify(accountVerificationBodyService, never()).getVerificationBodyNameInfoByAccount(anyLong());
    }
    
    @Test
    void getActiveVerificationBodies() throws Exception {
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        String verificationBodyName = "vb";
        VerificationBodyNameInfoDTO vb = VerificationBodyNameInfoDTO.builder().id(verificationBodyId).name(verificationBodyName).build();
        List<VerificationBodyNameInfoDTO> vbs = List.of(vb);
        
        when(accountVerificationBodyService.getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId))
            .thenReturn(vbs);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId + "/active-verification-bodies")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(verificationBodyId))
            .andExpect(jsonPath("$[0].name").value(verificationBodyName));

        verify(accountVerificationBodyService, times(1)).getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId);
    }
    
    @Test
    void getActiveVerificationBodies_forbidden() throws Exception {
        Long accountId = 1L;
        AppUser user = AppUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getActiveVerificationBodies", Long.toString(accountId), null, null);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + "/" + accountId + "/active-verification-bodies")
                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isForbidden());

        verify(accountVerificationBodyService, never()).getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId);
    }
    
    @Test
    void appointVerificationBodyToAccount() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        AppointVerificationBodyDTO vb = 
                AppointVerificationBodyDTO.builder().verificationBodyId(verificationBodyId).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/appoint-verification-body")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(vb)))
                    .andExpect(status().isNoContent());
        
        verify(accountVerificationBodyAppointService, times(1)).appointVerificationBodyToAccount(verificationBodyId, accountId);
    }
    
    @Test
    void appointVerificationBodyToAccount_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        AppointVerificationBodyDTO vb = 
                AppointVerificationBodyDTO.builder().verificationBodyId(verificationBodyId).build();
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "appointVerificationBodyToAccount", String.valueOf(accountId), null, null);
        
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + "/" + accountId + "/appoint-verification-body")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(vb)))
                    .andExpect(status().isForbidden());
        
        verify(accountVerificationBodyAppointService, never())
            .appointVerificationBodyToAccount(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void replaceVerificationBodyToAccount() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        AppointVerificationBodyDTO vb = AppointVerificationBodyDTO.builder().verificationBodyId(verificationBodyId).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(CONTROLLER_PATH + "/" + accountId + "/appoint-verification-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vb)))
                .andExpect(status().isNoContent());

        verify(accountVerificationBodyAppointService, times(1))
                .replaceVerificationBodyToAccount(verificationBodyId, accountId);
    }

    @Test
    void replaceVerificationBodyToAccount_forbidden() throws Exception {
        AppUser user = AppUser.builder().build();
        Long accountId = 1L;
        Long verificationBodyId = 1L;
        AppointVerificationBodyDTO vb = AppointVerificationBodyDTO.builder().verificationBodyId(verificationBodyId).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "replaceVerificationBodyToAccount", String.valueOf(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(CONTROLLER_PATH + "/" + accountId + "/appoint-verification-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vb)))
                .andExpect(status().isForbidden());

        verify(accountVerificationBodyAppointService, never())
                .replaceVerificationBodyToAccount(Mockito.anyLong(), Mockito.anyLong());
    }
}
