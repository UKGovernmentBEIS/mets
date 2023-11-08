package uk.gov.pmrv.api.web.controller.emp;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpIssuingAuthorityQueryService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

@ExtendWith(MockitoExtension.class)
class EmpIssuingAuthorityControllerTest {

    @InjectMocks
    private EmpIssuingAuthorityController empIssuingAuthorityController;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private EmpIssuingAuthorityQueryService empIssuingAuthorityQueryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
                authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(empIssuingAuthorityController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        empIssuingAuthorityController = (EmpIssuingAuthorityController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(empIssuingAuthorityController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getEmpIssuingAuthorityNames() throws Exception {

        when(empIssuingAuthorityQueryService.getAllIssuingAuthorityNames())
                .thenReturn(List.of("name"));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1.0/issuing-authority")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value("name"));

        verify(empIssuingAuthorityQueryService, times(1)).getAllIssuingAuthorityNames();
    }

    @Test
    void getEmpIssuingAuthorityNames_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("userId")
                .roleType(OPERATOR)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1.0/issuing-authority")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empIssuingAuthorityQueryService);
    }
}
