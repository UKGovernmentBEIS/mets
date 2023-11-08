package uk.gov.pmrv.api.web.controller.reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.reporting.domain.dto.ReportingYearsDTO;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportingAccountControllerTest {

    private static final String REPORTING_BASE_CONTROLLER_PATH = "/v1.0/reporting/account";

    @InjectMocks
    private ReportingAccountController reportingAccountController;

    @Mock
    private ReportableEmissionsService reportableEmissionsService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(reportingAccountController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        reportingAccountController = (ReportingAccountController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(reportingAccountController)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getReportableEmissions() throws Exception {
        final long accountId = 1L;
        final Year year = Year.of(2022);
        final BigDecimal emissions = BigDecimal.valueOf(2000);
        final ReportingYearsDTO reportingYears = ReportingYearsDTO.builder()
                .years(Set.of(year))
                .build();

        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(reportableEmissionsService.getReportableEmissions(accountId, Set.of(year)))
                .thenReturn(Map.of(year, emissions));

        // Invoke
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(REPORTING_BASE_CONTROLLER_PATH + "/" + accountId + "/emissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reportingYears)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['2022']").value(emissions));

        verify(reportableEmissionsService, times(1)).getReportableEmissions(accountId, Set.of(year));
    }

    @Test
    void getReportableEmissions_forbidden() throws Exception {
        final long accountId = 1L;
        final Year year = Year.of(2022);
        final ReportingYearsDTO reportingYears = ReportingYearsDTO.builder()
                .years(Set.of(year))
                .build();
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(currentUser, "getReportableEmissions", String.valueOf(accountId));

        // Invoke
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(REPORTING_BASE_CONTROLLER_PATH + "/" + accountId + "/emissions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reportingYears)))
                .andExpect(status().isForbidden());

        verify(reportableEmissionsService, never()).getReportableEmissions(anyLong(), any());
    }
}
