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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;

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
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AviationAccountViewController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice()).build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getInstallationAccountById() throws Exception {
        final Long accountId = 1L;
        final String empId = "empId";
        final AppUser user = AppUser.builder().userId("userId").build();
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
        final AppUser user = AppUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getAviationAccountById", Long.toString(invalidAccountId), null, null);

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
        final AppUser user = AppUser.builder().userId("userId").build();

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
            .id(accountId)
            .name("name")
            .emissionTradingScheme(EmissionTradingScheme.UK_ETS_AVIATION)
            .status(AviationAccountStatus.NEW)
            .build();

        when(orchestrator.getAccountHeaderInfo(accountId)).thenReturn(accountHeaderInfo);

        AviationAccountHeaderInfoDTO expected = AviationAccountHeaderInfoDTO.builder()
            .id(accountId)
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
        AppUser user = AppUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getAviationAccountHeaderInfoById", Long.toString(accountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(orchestrator);
    }
}
