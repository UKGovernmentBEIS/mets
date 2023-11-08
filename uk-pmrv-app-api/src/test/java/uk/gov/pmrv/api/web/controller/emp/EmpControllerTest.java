package uk.gov.pmrv.api.web.controller.emp;

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
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpAttachmentService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmpDocumentService;
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
class EmpControllerTest {
    private static final String EMP_CONTROLLER_PATH = "/v1.0/emps";

    private MockMvc mockMvc;

    @InjectMocks
    private EmpController empController;

    @Mock
    private EmpAttachmentService empAttachmentService;

    @Mock
    private EmpDocumentService empDocumentService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(empController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        empController = (EmpController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(empController)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void generateGetEmpAttachmentToken() throws Exception {
        String empId = "1";
        UUID attachmentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(empAttachmentService.generateGetFileAttachmentToken(empId, attachmentUuid)).thenReturn(
                expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EMP_CONTROLLER_PATH + "/" + empId + "/attachments")
                        .param("uuid", attachmentUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(empAttachmentService, times(1)).generateGetFileAttachmentToken(empId, attachmentUuid);
    }

    @Test
    void generateGetEmpAttachmentToken_forbidden() throws Exception {
        Long empId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        PmrvUser authUser = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(authUser, "generateGetEmpAttachmentToken", String.valueOf(empId));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EMP_CONTROLLER_PATH + "/" + empId + "/attachments")
                        .param("uuid", attachmentUuid.toString()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empAttachmentService);
    }

    @Test
    void generateGetEmpDocumentToken() throws Exception {
        String empId = "1";
        UUID documentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(empDocumentService.generateGetFileDocumentToken(empId, documentUuid)).thenReturn(
                expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EMP_CONTROLLER_PATH + "/" + empId + "/document")
                        .param("uuid", documentUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(empDocumentService, times(1)).generateGetFileDocumentToken(empId, documentUuid);
    }

    @Test
    void generateGetEmpDocumentToken_forbidden() throws Exception {
        Long empId = 1L;
        UUID documentUuid = UUID.randomUUID();
        PmrvUser authUser = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(authUser, "generateGetEmpDocumentToken", String.valueOf(empId));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(EMP_CONTROLLER_PATH + "/" + empId + "/document")
                        .param("uuid", documentUuid.toString()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(empDocumentService);
    }

}