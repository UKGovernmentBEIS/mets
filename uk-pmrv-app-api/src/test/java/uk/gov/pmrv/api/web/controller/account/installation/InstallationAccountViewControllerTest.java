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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountDetailsDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.permit.domain.dto.PermitDetailsDTO;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;

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
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private InstallationAccountQueryOrchestrator orchestrator;

    private AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @BeforeEach
    public void setUp() {
        authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

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
    	setupAppUser();
        final Long accountId = 1L;
        final InstallationAccountDetailsDTO installationAccountPermitDTO =
                InstallationAccountDetailsDTO.builder().accountPermitDto(InstallationAccountPermitDTO.builder()
                                .account(InstallationAccountDTO.builder()
                                        .id(accountId)
                                        .build())
                                .permit(PermitDetailsDTO.builder()
                                        .id("permitId")
                                        .build())
                                .build())
                        .latestAlrFile(FileInfoDTO.builder().build())
                        .build();
        when(orchestrator.getAccountDetails(accountId)).thenReturn(installationAccountPermitDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + accountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountPermitDto.account.id").value(accountId))
            .andExpect(jsonPath("$.accountPermitDto.permit.id").value("permitId"));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountDetails(accountId);
    }

    @Test
    void getInstallationAccountById_account_forbidden() throws Exception {
    	AppUser user = setupAppUser();
        final long invalidAccountId = 1L;
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getInstallationAccountById", Long.toString(invalidAccountId), null, null);

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(orchestrator, never()).getAccountWithPermit(anyLong());
    }

    @Test
    void getInstallationAccountById_account_not_found() throws Exception {
    	setupAppUser();
        final Long invalidAccountId = 1L;
        when(orchestrator.getAccountDetails(invalidAccountId))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        mockMvc.perform(
                MockMvcRequestBuilders
                    .get(CONTROLLER_PATH + "/" + invalidAccountId)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(orchestrator, times(1)).getAccountDetails(invalidAccountId);
    }

    @Test
    void getInstallationAccountHeaderInfoById() throws Exception {
    	setupAppUser();
        Long accountId = 1L;
        String accountName = "accountName";
        InstallationAccountStatus accountStatus = InstallationAccountStatus.LIVE;
        InstallationAccountHeaderInfoDTO accountHeaderInfo =
            InstallationAccountHeaderInfoDTO.builder()
                .name(accountName)
                .status(accountStatus)
                .id(accountId)
                .build();
        when(orchestrator.getAccountHeaderInfoWithPermitId(accountId)).thenReturn(Optional.of(accountHeaderInfo));

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(accountName))
            .andExpect(jsonPath("$.status").value(accountStatus.name()))
            .andExpect(jsonPath("$.id").value(accountId));

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, times(1)).getAccountHeaderInfoWithPermitId(accountId);
    }

    @Test
    void getInstallationAccountHeaderInfoById_forbidden() throws Exception {
    	AppUser user = setupAppUser();
        String accountId = "1";
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "getInstallationAccountHeaderInfoById", accountId, null, null);

        mockMvc.perform(
            MockMvcRequestBuilders
                .get(CONTROLLER_PATH + "/" + accountId + "/header-info")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(orchestrator);
    }
    
    private AppUser setupAppUser() {
		AppUser user = AppUser.builder().userId("authId").build();
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		return user;
	}

}
