package uk.gov.pmrv.api.web.controller.workflow.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersOperatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.VERIFIER;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersControllerTest {

    private static final String BASE_PATH = "/v1.0/installation/items";
    private static final String ASSIGNED_TO_OTHERS_PATH = "assigned-to-others";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToOthersOperatorService itemAssignedToOthersOperatorService;

    @Mock
    private ItemAssignedToOthersRegulatorService itemAssignedToOthersRegulatorService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToOthersService> services = List.of(itemAssignedToOthersOperatorService, itemAssignedToOthersRegulatorService);
        ItemAssignedToOthersController itemController = new ItemAssignedToOthersController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemAssignedToOthersController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getItemsAssignedToOthers_operator() throws Exception {
        final AccountType accountType = AccountType.INSTALLATION;
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToOthersOperatorService.getItemsAssignedToOthers(appUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(itemAssignedToOthersOperatorService, times(1))
                .getItemsAssignedToOthers(appUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthers(any(), any(), any(PagingRequest.class));
    }

    @Test
    void getItemsAssignedToOthers_regulator() throws Exception {
        final AccountType accountType = AccountType.INSTALLATION;
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemAssignedToOthersRegulatorService.getItemsAssignedToOthers(appUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build()))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);
        when(itemAssignedToOthersRegulatorService.getRoleType()).thenReturn(RoleTypeConstants.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemAssignedToOthersOperatorService, never())
                .getItemsAssignedToOthers(any(), any(), any(PagingRequest.class));
        verify(itemAssignedToOthersRegulatorService, times(1))
                .getItemsAssignedToOthers(appUser, accountType, PagingRequest.builder().pageNumber(0L).pageSize(10L).build());
    }

    @Test
    void getItemsAssignedToOthers_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToOthersOperatorService, never())
            .getItemsAssignedToOthers(any(), any(), any(PagingRequest.class));
        verify(itemAssignedToOthersRegulatorService, never())
            .getItemsAssignedToOthers(any(), any(), any(PagingRequest.class));
    }
}
