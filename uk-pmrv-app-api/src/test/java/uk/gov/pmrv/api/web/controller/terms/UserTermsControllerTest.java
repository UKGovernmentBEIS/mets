package uk.gov.pmrv.api.web.controller.terms;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.terms.userterms.UserTermsService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserTermsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserTermsController userTermsController;

    @Mock
    private UserTermsService userTermsService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private ObjectMapper objectMapper;

    public static final String USER_TERMS_CONTROLLER_PATH = "/v1.0/user-terms";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect authorizedAspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(userTermsController);
        aspectJProxyFactory.addAspect(authorizedAspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        userTermsController = (UserTermsController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(userTermsController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void editUserTerms() throws Exception {
        final String userId = "userId";
        UserTermsVersionUpdateDTO updateTermsDTO = new UserTermsVersionUpdateDTO(Short.valueOf("1"));
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(AppUser.builder().userId(userId).build());

        mockMvc.perform(MockMvcRequestBuilders.patch(USER_TERMS_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTermsDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));

        verify(userTermsService, times(1)).updateUserTerms(userId, updateTermsDTO.getVersion());
    }

    @Test
    void getUserTerms() throws Exception {
        final String userId = "userId";
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(AppUser.builder().userId(userId).build());
        when(userTermsService.getUserTerms(userId)).thenReturn(Optional.of((short)1));

        mockMvc.perform(MockMvcRequestBuilders.get(USER_TERMS_CONTROLLER_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termsVersion").value(1));

        verify(userTermsService, times(1)).getUserTerms(userId);
    }
}