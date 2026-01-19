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
import org.springframework.security.web.FilterChainProxy;
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
import uk.gov.pmrv.api.account.fileattachment.service.AccountFileAttachmentAttachmentService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AccountFileAttachmentControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/accounts/{accountId}/file-attachments";

    private MockMvc mockMvc;

    @InjectMocks
    private AccountFileAttachmentController controller;

    @Mock
    private AccountFileAttachmentAttachmentService accountFileAttachmentAttachmentService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect =
                new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (AccountFileAttachmentController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }


    @Test
    void generateGetFileAccountFileAttachmentToken_success() throws Exception {
        setupAppUser();

        Long accountId = 1L;
        String uuid = "UUID123";

        FileToken expectedToken =
                FileToken.builder().token("token-value").build();

        when(accountFileAttachmentAttachmentService
                .generateGetFileAttachmentToken(accountId, uuid))
                .thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH, accountId)
                        .param("uuid", uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-value"));

        verify(accountFileAttachmentAttachmentService)
                .generateGetFileAttachmentToken(accountId, uuid);
    }

    @Test
    void generateGetFileAccountFileAttachmentToken_forbidden() throws Exception {
        AppUser authUser = setupAppUser();

        Long accountId = 1L;
        String uuid = "UUID123";

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(
                        authUser,
                        "generateGetFileAccountFileAttachmentToken",
                        String.valueOf(accountId),
                        null,
                        null
                );

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH, accountId)
                        .param("uuid", uuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(accountFileAttachmentAttachmentService);
    }

    private AppUser setupAppUser() {
        AppUser user = AppUser.builder().userId("authId").build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        return user;
    }
}
