package uk.gov.pmrv.api.web.controller.workflow.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemOperatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemOperatorService itemOperatorService;

    @Mock
    private ItemRegulatorService itemRegulatorService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    private MockMvc mockMvc;
    private static final String BASE_PATH = "/v1.0/items";

    @BeforeEach
    public void setUp() {
        List<ItemService> services = List.of(itemOperatorService, itemRegulatorService);
        ItemController itemController = new ItemController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getItemsByRequest_operator() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemOperatorService.getItemsByRequest(appUser, requestId)).thenReturn(itemDTOResponse);
        when(itemOperatorService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemOperatorService, times(1))
                .getItemsByRequest(appUser, requestId);
        verify(itemRegulatorService, never())
                .getItemsByRequest(any(), anyString());
    }

    @Test
    void getItemsByRequest_regulator() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(itemRegulatorService.getItemsByRequest(appUser, requestId)).thenReturn(itemDTOResponse);
        when(itemOperatorService.getRoleType()).thenReturn(RoleTypeConstants.OPERATOR);
        when(itemRegulatorService.getRoleType()).thenReturn(RoleTypeConstants.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRegulatorService, times(1))
                .getItemsByRequest(appUser, requestId);
        verify(itemOperatorService, never())
                .getItemsByRequest(any(), anyString());
    }

    @Test
    void getItemsByRequest_forbidden() throws Exception {
        final String requestId = "1";
        AppUser appUser = AppUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getItemsByRequest", requestId, null, null);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(itemOperatorService, never())
                .getItemsByRequest(any(), anyString());
        verify(itemRegulatorService, never())
                .getItemsByRequest(any(), anyString());
    }
}
