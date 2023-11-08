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
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
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
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    private MockMvc mockMvc;
    private static final String BASE_PATH = "/v1.0/items";

    @BeforeEach
    public void setUp() {
        List<ItemService> services = List.of(itemOperatorService, itemRegulatorService);
        ItemController itemController = new ItemController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getItemsByRequest_operator() throws Exception {
        final String requestId = "1";
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemOperatorService.getItemsByRequest(pmrvUser, requestId)).thenReturn(itemDTOResponse);
        when(itemOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemOperatorService, times(1))
                .getItemsByRequest(pmrvUser, requestId);
        verify(itemRegulatorService, never())
                .getItemsByRequest(any(), anyString());
    }

    @Test
    void getItemsByRequest_regulator() throws Exception {
        final String requestId = "1";
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder().totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemRegulatorService.getItemsByRequest(pmrvUser, requestId)).thenReturn(itemDTOResponse);
        when(itemOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + requestId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemRegulatorService, times(1))
                .getItemsByRequest(pmrvUser, requestId);
        verify(itemOperatorService, never())
                .getItemsByRequest(any(), anyString());
    }

    @Test
    void getItemsByRequest_forbidden() throws Exception {
        final String requestId = "1";
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "getItemsByRequest", String.valueOf(requestId));

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
