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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.AvailableRequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AvailableRequestControllerTest {

    private static final String BASE_PATH = "/v1.0/requests/available-workflows";

    private MockMvc mockMvc;

    @InjectMocks
    private AvailableRequestController controller;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AvailableRequestService availableRequestService;

    @BeforeEach
    public void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AvailableRequestController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getAvailableAccountWorkflows() throws Exception {
        final Long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("id").build();
        final Map<RequestCreateActionType, RequestCreateValidationResult> results =
                Map.of(RequestCreateActionType.PERMIT_SURRENDER,
                        RequestCreateValidationResult.builder().valid(true).build());

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(availableRequestService.getAvailableAccountWorkflows(accountId, appUser)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/permit/" + accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"PERMIT_SURRENDER\":{\"valid\":true}}"));

        verify(availableRequestService, times(1)).getAvailableAccountWorkflows(accountId, appUser);
    }

    @Test
    void getAvailableAerWorkflows() throws Exception {
        final String requestId = "AEM-1";
        final AppUser appUser = AppUser.builder().userId("id").build();
        final Map<RequestCreateActionType, RequestCreateValidationResult> results =
                Map.of(RequestCreateActionType.AER,
                        RequestCreateValidationResult.builder().valid(true).build());

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(availableRequestService.getAvailableAerWorkflows(requestId, appUser)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "/reporting/aer/" + requestId))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"AER\":{\"valid\":true}}"));

        verify(availableRequestService, times(1)).getAvailableAerWorkflows(requestId, appUser);
    }
}
