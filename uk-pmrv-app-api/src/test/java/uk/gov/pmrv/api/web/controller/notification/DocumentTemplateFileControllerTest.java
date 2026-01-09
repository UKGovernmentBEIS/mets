package uk.gov.pmrv.api.web.controller.notification;

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
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateFileService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateFileControllerTest {

    private static final String DOCUMENT_TEMPLATE_FILES_CONTROLLER_PATH = "/v1.0/document-template-files";

    private MockMvc mockMvc;

    @InjectMocks
    private DocumentTemplateFileController documentTemplateFileController;

    @Mock
    private DocumentTemplateFileService documentTemplateFileService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect
            authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(documentTemplateFileController);
        aspectJProxyFactory.addAspect(authorizedAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        documentTemplateFileController = (DocumentTemplateFileController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(documentTemplateFileController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .build()
        ;
    }

    @Test
    void generateGetDocumentTemplateFileToken() throws Exception {
        Long documentTemplateId = 1L;
        UUID fileUuid = UUID.randomUUID();
        FileToken fileToken = FileToken.builder().token("token").tokenExpirationMinutes(10L).build();

        when(documentTemplateFileService.generateGetFileDocumentTemplateToken(documentTemplateId, fileUuid)).thenReturn(fileToken);

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_FILES_CONTROLLER_PATH + "/" + documentTemplateId)
            .param("fileUuid", String.valueOf(fileUuid))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(fileToken.getToken()))
            .andExpect(jsonPath("$.tokenExpirationMinutes").value(fileToken.getTokenExpirationMinutes()));

        verify(documentTemplateFileService, times(1))
            .generateGetFileDocumentTemplateToken(documentTemplateId, fileUuid);
    }

    @Test
    void generateGetDocumentTemplateFileToken_forbidden() throws Exception {
        Long documentTemplateId = 1L;
        UUID fileUuid = UUID.randomUUID();
        AppUser appUser = AppUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "generateGetDocumentTemplateFileToken", String.valueOf(documentTemplateId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
            .get(DOCUMENT_TEMPLATE_FILES_CONTROLLER_PATH + "/" + documentTemplateId)
            .param("fileUuid", String.valueOf(fileUuid)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(documentTemplateFileService);
    }
}