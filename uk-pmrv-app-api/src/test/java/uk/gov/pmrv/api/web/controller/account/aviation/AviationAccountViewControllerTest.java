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
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryIntegrationPreviewService;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryViewDTO;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountEmpDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.dto.AviationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;

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

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AviationAccountRegistryIntegrationPreviewService aviationAccountRegistryIntegrationPreviewService;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);


        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

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
    	AppUser user = setupAppUser();
        final Long accountId = 1L;
        final String empId = "empId";
        final AviationAccountEmpDTO aviationAccountEmpDTO =
                AviationAccountEmpDTO.builder()
                        .aviationAccount(AviationAccountDTO.builder()
                                .id(accountId)
                                .build())
                        .emp(EmpDetailsDTO.builder()
                                .id(empId)
                                .build())
                        .build();
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
        AppUser user = setupAppUser();
        final long invalidAccountId = 1L;

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
    	AppUser user = setupAppUser();
        final Long invalidAccountId = 1L;

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
    	setupAppUser();
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
    	AppUser user = setupAppUser();
        Long accountId = 1L;

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

    @Test
    void getAviationAccountViewForRegistry() throws Exception {
    	final AppUser user = AppUser.builder()
    			.userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
    	
        String requestId = "1";
        AviationAccountRegistryViewDTO aviationAccountRegistryViewDTO = AviationAccountRegistryViewDTO.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                        .emitterId("1")
                        .regulator("EA")
                        .operatorName("operatorName")
                        .build())
                .build();

        when(aviationAccountRegistryIntegrationPreviewService.getAviationAccountRegistryView(requestId)).thenReturn(aviationAccountRegistryViewDTO);


        AviationAccountRegistryViewDTO expected = AviationAccountRegistryViewDTO.builder()
                .operatorDetails(AviationOperatorDetails.builder()
                        .emitterId("1")
                        .regulator("EA")
                        .operatorName("operatorName")
                        .build())
                .build();

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(CONTROLLER_PATH + "/" + requestId + "/registry-view")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AviationAccountRegistryViewDTO actualResult =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), AviationAccountRegistryViewDTO.class);


        assertEquals(expected, actualResult);

        verify(aviationAccountRegistryIntegrationPreviewService, times(1)).getAviationAccountRegistryView(requestId);
    }

    @Test
    void getAviationAccountViewForRegistry_forbidden() throws Exception {
        String requestId = "1";
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.OPERATOR)
                .build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[] {RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/" + requestId + "/registry-view")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(aviationAccountRegistryIntegrationPreviewService);
    }

    private AppUser setupAppUser() {
		AppUser user = AppUser.builder().userId("authId").build();
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		return user;
	}

}
