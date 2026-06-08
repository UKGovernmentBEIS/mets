package uk.gov.pmrv.api.web.controller.account;


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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsHistoryListResponse;
import uk.gov.pmrv.api.account.service.AccountDetailsHistoryService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
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
class AccountDetailsHistoryControllerTest {

    private static final String HISTORY_CONTROLLER_PATH = "/v1.0/account-details-history";

    private MockMvc mockMvc;

    @InjectMocks
    private AccountDetailsHistoryController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AccountDetailsHistoryService accountDetailsHistoryService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (AccountDetailsHistoryController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getAccountDetailsHistory() throws Exception {
        final long accountId = 1L;
        final int page = 0;
        final int size = 10;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();

        final AccountDetailsHistoryListResponse response = AccountDetailsHistoryListResponse.builder()
                .total(1L)
                .accountDetailsHistoryList(List.of(
                        AccountDetailsHistoryDTO.builder()
                                .changedBy("John Doe")
                                .reason("Update reason")
                                .build()
                ))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(accountDetailsHistoryService.getAccountDetailsHistory(accountId, page, size)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(HISTORY_CONTROLLER_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.accountDetailsHistoryList[0].reason").value("Update reason"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountDetailsHistoryService, times(1)).getAccountDetailsHistory(accountId, page, size);
    }

    @Test
    void getAccountDetailsHistory_forbidden() throws Exception {
        final long accountId = 1L;
        final AppUser user = AppUser.builder().userId("authId").roleType(RoleTypeConstants.OPERATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(user, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(HISTORY_CONTROLLER_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(accountDetailsHistoryService, never()).getAccountDetailsHistory(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getAccountDetailsHistory_invalid_parameters() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(HISTORY_CONTROLLER_PATH)
                        .param("accountId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountDetailsHistoryService);
    }
}