package uk.gov.pmrv.api.web.controller.aviationreporting;

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
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportableEmissionsDTO;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportingYearsDTO;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingControllerTest {

    private static final String BASE_CONTROLLER_PATH = "/v1.0/aviation-reporting/account";

    @InjectMocks
    private AviationAccountReportingController controller;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

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

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AviationAccountReportingController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAviationAccountReportableEmissions() throws Exception {
        Long accountId = 1L;
        Year year2022 = Year.of(2022);
        AviationReportableEmissionsDTO reportableEmissions = AviationReportableEmissionsDTO.builder()
                .totalReportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                        .reportableEmissions(BigDecimal.valueOf(2000))
                        .build())
            .isExempted(false)
            .build();
        AviationReportingYearsDTO reportingYears = AviationReportingYearsDTO.builder()
            .years(Set.of(year2022))
            .build();

        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(aviationReportableEmissionsService
            .getReportableEmissions(accountId, Set.of(year2022)))
            .thenReturn(Map.of(year2022, reportableEmissions));

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(BASE_CONTROLLER_PATH + "/" + accountId + "/emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reportingYears)))
            .andExpect(status().isOk());

        verify(aviationReportableEmissionsService, times(1))
            .getReportableEmissions(accountId, Set.of(year2022));
    }

    @Test
    void getAviationAccountReportableEmissions_forbidden() throws Exception {
        Long accountId = 1L;
        AviationReportingYearsDTO reportingYears = AviationReportingYearsDTO.builder()
            .years(Set.of(Year.of(2022)))
            .build();
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "getAviationAccountReportableEmissions", String.valueOf(accountId));

        // Invoke
        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(BASE_CONTROLLER_PATH + "/" + accountId + "/emissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(reportingYears)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(aviationReportableEmissionsService);
    }
}