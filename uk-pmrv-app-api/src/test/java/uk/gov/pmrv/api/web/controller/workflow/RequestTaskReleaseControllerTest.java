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
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestTaskReleaseControllerTest {

    private static final String BASE_PATH = "/v1.0/tasks-assignment/release";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestTaskReleaseController controller;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private RequestTaskReleaseService requestTaskReleaseService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (RequestTaskReleaseController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .build();
    }

    @Test
    void releaseTask() throws Exception {
        AppUser appUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.REGULATOR).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doNothing().when(requestTaskReleaseService).releaseTaskById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(requestTaskReleaseService, times(1)).releaseTaskById(1L);
    }

    @Test
    void releaseTask_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("userId").roleType(RoleTypeConstants.REGULATOR).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "releaseTask", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + "/" + 1)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(requestTaskReleaseService, never()).releaseTaskById(anyLong());
    }
}
