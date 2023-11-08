package uk.gov.pmrv.api.web.controller.permit;

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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.service.PermitAttachmentService;
import uk.gov.pmrv.api.permit.service.PermitDocumentService;
import uk.gov.pmrv.api.token.FileToken;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PermitControllerTest {
    private static final String PERMIT_CONTROLLER_PATH = "/v1.0/permits";

    private MockMvc mockMvc;

    @InjectMocks
    private PermitController permitController;

    @Mock
    private PermitAttachmentService permitAttachmentService;

    @Mock
    private PermitDocumentService permitDocumentService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
            new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permitController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        permitController = (PermitController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(permitController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void generateGetPermitAttachmentToken() throws Exception {
        String permitId = "1";
        UUID attachmentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(permitAttachmentService.generateGetFileAttachmentToken(permitId, attachmentUuid)).thenReturn(
            expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/attachments")
                .param("uuid", attachmentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(permitAttachmentService, times(1)).generateGetFileAttachmentToken(permitId, attachmentUuid);
    }

    @Test
    void generateGetPermitAttachmentToken_forbidden() throws Exception {
        Long permitId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        PmrvUser authUser = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(authUser, "generateGetPermitAttachmentToken", String.valueOf(permitId));

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/attachments")
                .param("uuid", attachmentUuid.toString()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(permitAttachmentService);
    }

    @Test
    void generateGetPermitDocumentToken() throws Exception {
        String permitId = "1";
        UUID documentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(permitDocumentService.generateGetFileDocumentToken(permitId, documentUuid)).thenReturn(
            expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/document")
                .param("uuid", documentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(permitDocumentService, times(1)).generateGetFileDocumentToken(permitId, documentUuid);
    }

    @Test
    void generateGetPermitDocumentToken_forbidden() throws Exception {
        Long permitId = 1L;
        UUID documentUuid = UUID.randomUUID();
        PmrvUser authUser = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(authUser, "generateGetPermitDocumentToken", String.valueOf(permitId));

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/document")
                .param("uuid", documentUuid.toString()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(permitDocumentService);
    }
}
