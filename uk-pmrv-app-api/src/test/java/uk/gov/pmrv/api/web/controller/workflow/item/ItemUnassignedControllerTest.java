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

import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemUnassignedRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemUnassignedService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedControllerTest {

    private static final String BASE_PATH = "/v1.0/installation/items";
    private static final String UNASSIGNED = "unassigned";

    private static final String USER_ID = "user_id";
    private static final PagingRequest PAGING = PagingRequest.builder().pageNumber(0L).pageSize(10L).build();

    private MockMvc mockMvc;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private ItemUnassignedRegulatorService itemUnassignedRegulatorService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemUnassignedService> services = List.of(itemUnassignedRegulatorService);
        ItemUnassignedController itemController = new ItemUnassignedController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemUnassignedController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getUnassignedItems_regulator() throws Exception {
        final AccountType accountType = AccountType.INSTALLATION;
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemUnassignedRegulatorService.getUnassignedItems(pmrvUser, accountType, PAGING))
                .thenReturn(itemDTOResponse);
        when(itemUnassignedRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemUnassignedRegulatorService, times(1))
                .getUnassignedItems(pmrvUser, accountType, PAGING);
    }

    @Test
    void getUnassignedItems_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});


        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemUnassignedRegulatorService, never()).getUnassignedItems(any(), any(), any(PagingRequest.class));
    }

    private PmrvUser buildMockPmrvUser(RoleType roleType) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(ENGLAND)
            .build();

        return PmrvUser.builder()
            .userId(USER_ID)
            .authorities(List.of(pmrvAuthority))
            .roleType(roleType)
            .build();
    }
}
