package uk.gov.pmrv.api.web.controller.account.aviation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hibernate.validator.HibernateValidator;
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
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.support.GenericWebApplicationContext;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryCreationDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryListResponse;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusHistoryCreationService;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusHistoryQueryService;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.controller.utils.TestConstrainValidatorFactory;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusHistoryControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/aviation/accounts/reporting-status-history";

    @InjectMocks
    private AviationAccountReportingStatusHistoryController controller;

    @Mock
    private AviationAccountReportingStatusHistoryQueryService aviationAccountReportingStatusQueryService;

    @Mock
    private AviationAccountReportingStatusHistoryCreationService aviationAccountReportingStatusCreationService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

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
        controller = (AviationAccountReportingStatusHistoryController) aopProxy.getProxy();

        LocalValidatorFactoryBean validatorFactoryBean = mockValidatorFactoryBean();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setValidator(validatorFactoryBean)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private LocalValidatorFactoryBean mockValidatorFactoryBean() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        MockServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext context = new GenericWebApplicationContext(servletContext);

        context.refresh();
        validatorFactoryBean.setApplicationContext(context);
        TestConstrainValidatorFactory constraintValidatorFactory = new TestConstrainValidatorFactory(context);
        validatorFactoryBean.setConstraintValidatorFactory(constraintValidatorFactory);
        validatorFactoryBean.setProviderClass(HibernateValidator.class);
        validatorFactoryBean.afterPropertiesSet();
        return validatorFactoryBean;
    }

    @Test
    void getReportingStatusHistory() throws Exception {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();
        AviationAccountReportingStatusHistoryListResponse reportingStatusHistory = AviationAccountReportingStatusHistoryListResponse.builder()
            .reportingStatusHistoryList(List.of(AviationAccountReportingStatusHistoryDTO.builder()
                .status(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
                .reason("reason")
                .submitterName("submitterName")
                .submissionDate(LocalDateTime.now())
                .build()))
            .total(1L)
            .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(aviationAccountReportingStatusQueryService.getReportingStatusHistoryListResponse(accountId, 0, 1))
                .thenReturn(reportingStatusHistory);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/history")
                        .param("accountId", String.valueOf(accountId))
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("reportingStatusHistoryList[0].status").value(AviationAccountReportingStatus.REQUIRED_TO_REPORT.name()))
                .andExpect(jsonPath("reportingStatusHistoryList[0].reason").value("reason"))
                .andExpect(jsonPath("reportingStatusHistoryList[0].submitterName").value("submitterName"));

        verify(aviationAccountReportingStatusQueryService, times(1))
                .getReportingStatusHistoryListResponse(accountId, 0, 1);
    }

    @Test
    void getReportingStatusHistory_forbidden() throws Exception {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .roleType(RoleType.VERIFIER)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "getReportingStatusHistory", String.valueOf(accountId));

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/history")
                        .param("accountId", String.valueOf(accountId))
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountReportingStatusQueryService);
    }

    @Test
    void submitReportingStatus() throws Exception {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatus.REQUIRED_TO_REPORT).reason("reason").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(CONTROLLER_PATH + "/" + accountId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reportingStatusCreationDTO)))
                .andExpect(status().isNoContent());

        verify(aviationAccountReportingStatusCreationService, times(1))
                .submitReportingStatus(accountId,reportingStatusCreationDTO, pmrvUser);
    }

    @Test
    void submitReportingStatus_forbidden() throws Exception {
        Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        final AviationAccountReportingStatusHistoryCreationDTO reportingStatusCreationDTO =
                AviationAccountReportingStatusHistoryCreationDTO.builder().status(AviationAccountReportingStatus.REQUIRED_TO_REPORT).reason("reason").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "submitReportingStatus", String.valueOf(accountId));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(CONTROLLER_PATH + "/" + accountId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reportingStatusCreationDTO)))
                .andExpect(status().isForbidden());

        verify(aviationAccountReportingStatusCreationService, never())
                .submitReportingStatus(accountId, reportingStatusCreationDTO, pmrvUser);
    }
}
