package uk.gov.pmrv.api.web.controller.account.aviation;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AviationAccountViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/aviation/account";

    @InjectMocks
    private AviationAccountViewController controller;

    @Mock
    private AviationAccountEmpQueryOrchestrator orchestrator;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AviationAccountViewController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice()).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getInstallationAccountById() throws Exception {
        final Long accountId = 1L;
        final String empId = "empId";
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final AviationAccountEmpDTO aviationAccountEmpDTO =
                AviationAccountEmpDTO.builder()
                        .aviationAccount(AviationAccountDTO.builder()
                                .id(accountId)
                                .build())
                        .emp(EmpDetailsDTO.builder()
                                .id(empId)
                                .build())
                        .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAviationAccountWithEMP(accountId, user)).thenReturn(aviationAccountEmpDTO);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CONTROLLER_PATH + "/" + accountId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aviationAccount.id").value(accountId))
                .andExpect(jsonPath("$.emp.id").value(empId));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAviationAccountWithEMP(accountId, user);
    }

    @Test
    void getInstallationAccountById_account_forbidden() throws Exception {
        final long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "getAviationAccountById", Long.toString(invalidAccountId));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CONTROLLER_PATH + "/" + invalidAccountId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(orchestrator, never()).getAviationAccountWithEMP(anyLong(), any());
    }

    @Test
    void getInstallationAccountById_account_not_found() throws Exception {
        final Long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAviationAccountWithEMP(invalidAccountId, user))
                .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CONTROLLER_PATH + "/" + invalidAccountId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orchestrator, times(1)).getAviationAccountWithEMP(invalidAccountId, user);
    }

    @Test
    void getAviationAccountHeaderInfoById() throws Exception {
        Long accountId = 1L;
        AviationAccountHeaderInfoDTO accountHeaderInfo = AviationAccountHeaderInfoDTO.builder()
            .name("name")
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .status(AviationAccountStatus.NEW)
            .build();

        when(orchestrator.getAccountHeaderInfo(accountId)).thenReturn(accountHeaderInfo);

        AviationAccountHeaderInfoDTO expected = AviationAccountHeaderInfoDTO.builder()
            .name("name")
            .status(AviationAccountStatus.NEW)
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .build();

        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        AviationAccountHeaderInfoDTO actualResult =
            objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AviationAccountHeaderInfoDTO.class);

        assertEquals(expected, actualResult);

        verify(orchestrator, times(1)).getAccountHeaderInfo(accountId);
    }

    @Test
    void getAviationAccountHeaderInfoById_forbidden() throws Exception {
        Long accountId = 1L;
        PmrvUser user = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getAviationAccountHeaderInfoById", Long.toString(accountId));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(orchestrator);
    }
}
