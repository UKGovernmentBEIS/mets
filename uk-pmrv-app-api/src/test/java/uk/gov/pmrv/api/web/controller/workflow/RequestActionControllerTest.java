package uk.gov.pmrv.api.web.controller.workflow;

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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.application.requestaction.RequestActionQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

import java.util.List;

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
class RequestActionControllerTest {
    
    private static final String BASE_PATH = "/v1.0/request-actions";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestActionController controller;
    
    @Mock
    private AppSecurityComponent pmrvSecurityComponent;
    
    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;
    
    @Mock
    private RequestActionQueryService requestActionQueryService;
    
    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (RequestActionController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getRequestActionById() throws Exception {
        AppUser user = AppUser.builder().userId("user").build();
        Long requestActionId = 1L;
        RequestActionDTO requestActionDTO = RequestActionDTO.builder().id(requestActionId)
                .submitter("fn ln").build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(requestActionQueryService.getRequestActionById(requestActionId, user)).thenReturn(requestActionDTO);
        mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_PATH + "/" + requestActionId)
                                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestActionId))
                .andExpect(jsonPath("$.submitter").value("fn ln"));
        
        verify(requestActionQueryService, times(1)).getRequestActionById(requestActionId, user);
    }
    
    @Test
    void getRequestActionById_forbidden() throws Exception {
        AppUser user = AppUser.builder().userId("user").build();
        Long requestActionId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getRequestActionById", String.valueOf(requestActionId), null, null);

        mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_PATH + "/" + requestActionId)
                                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestActionQueryService, never()).getRequestActionById(anyLong(), any());
    }

    @Test
    void getRequestActionsByRequestId() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();

        RequestActionInfoDTO requestActionInfoDTO = RequestActionInfoDTO.builder().id(1L).build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(requestActionQueryService.getRequestActionsByRequestId("2", appUser)).thenReturn(List.of(requestActionInfoDTO));

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH)
                .queryParam("requestId", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getRequestActionsByRequestId_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("id").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getRequestActionsByRequestId", "2", null, null);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH)
                .queryParam("requestId", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(requestActionQueryService, never()).getRequestActionsByRequestId(anyString(), any());
    }
}
