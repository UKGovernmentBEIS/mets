package uk.gov.pmrv.api.web.controller.account.installation;

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
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountPermitQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Optional;

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
class InstallationAccountViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/installation/account";

    private MockMvc mockMvc;

    @InjectMocks
    private InstallationAccountViewController accountViewController;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private InstallationAccountPermitQueryOrchestrator orchestrator;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(accountViewController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        accountViewController = (InstallationAccountViewController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(accountViewController)
            .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @Test
    void getInstallationAccountById() throws Exception {
        final Long accountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final InstallationAccountPermitDTO installationAccountPermitDTO =
        		InstallationAccountPermitDTO.builder()
        		.account(InstallationAccountDTO.builder()
        				.id(accountId)
        				.build())
        		.permit(PermitDetailsDTO.builder()
        				.id("permitId")
        				.build())
                .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountWithPermit(accountId)).thenReturn(installationAccountPermitDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.account.id").value(accountId))
            .andExpect(jsonPath("$.permit.id").value("permitId"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountWithPermit(accountId);
    }

    @Test
    void getInstallationAccountById_account_forbidden() throws Exception {
        final long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getInstallationAccountById", Long.toString(invalidAccountId));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(orchestrator, never()).getAccountWithPermit(anyLong());
    }

    @Test
    void getInstallationAccountById_account_not_found() throws Exception {
        final Long invalidAccountId = 1L;
        final PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountWithPermit(invalidAccountId))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(orchestrator, times(1)).getAccountWithPermit(invalidAccountId);
    }

    @Test
    void getInstallationAccountHeaderInfoById() throws Exception {
        Long accountId = 1L;
        String accountName = "accountName";
        InstallationAccountStatus accountStatus = InstallationAccountStatus.LIVE;
        PmrvUser user = PmrvUser.builder().userId("userId").build();
        InstallationAccountHeaderInfoDTO accountHeaderInfo =
            InstallationAccountHeaderInfoDTO.builder()
                .name(accountName)
                .status(accountStatus)
                .build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(orchestrator.getAccountHeaderInfoWithPermitId(accountId)).thenReturn(Optional.of(accountHeaderInfo));

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(accountName))
            .andExpect(jsonPath("$.status").value(accountStatus.name()));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountHeaderInfoWithPermitId(accountId);
    }

    @Test
    void getInstallationAccountHeaderInfoById_forbidden() throws Exception {
        String accountId = "1";
        PmrvUser user = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(user, "getInstallationAccountHeaderInfoById", accountId);

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(orchestrator);
    }

}
