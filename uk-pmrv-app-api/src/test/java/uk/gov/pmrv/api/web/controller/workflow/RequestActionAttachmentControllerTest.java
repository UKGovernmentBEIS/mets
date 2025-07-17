package uk.gov.pmrv.api.web.controller.workflow;

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
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.application.attachment.requestaction.RequestActionAttachmentService;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestActionAttachmentControllerTest {

    private static final String BASE_PATH = "/v1.0/request-action-attachments";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestActionAttachmentController controller;

    @Mock
    private RequestActionAttachmentService requestActionAttachmentService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (RequestActionAttachmentController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void generateRequestActionGetFileAttachmentToken() throws Exception {
        Long requestActionId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(requestActionAttachmentService.generateGetFileAttachmentToken(requestActionId, attachmentUuid)).thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + requestActionId)
            .param("attachmentUuid", attachmentUuid.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(requestActionAttachmentService, times(1)).generateGetFileAttachmentToken(requestActionId, attachmentUuid);
    }

    @Test
    void generateRequestActionGetFileAttachmentToken_forbidden() throws Exception {
        Long requestActionId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        AppUser appUser = AppUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "generateRequestActionGetFileAttachmentToken", String.valueOf(requestActionId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + requestActionId)
            .param("attachmentUuid", attachmentUuid.toString()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(requestActionAttachmentService);
    }
}