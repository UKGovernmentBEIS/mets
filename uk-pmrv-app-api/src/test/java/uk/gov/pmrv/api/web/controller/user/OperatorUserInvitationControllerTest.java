package uk.gov.pmrv.api.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInvitationService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OperatorUserInvitationControllerTest {

    public static final String OPERATOR_USER_CONTROLLER_REGISTRATION_BASE_PATH = "/v1.0/operator-users/invite";
    public static final String ADD_TO_ACCOUNT_PATH = "/account";
    private static final String OPERATOR_USER_EMAIL = "operator_user_email";
    private static final String OPERATOR_USER_FNAME = "operator_user_fname";
    private static final String OPERATOR_USER_LNAME = "operator_user_lname";
    private static final String OPERATOR = "operator";

    @InjectMocks
    private OperatorUserInvitationController operatorUserInvitationController;

    @Mock
    private OperatorUserInvitationService operatorUserInvitationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private AppUserArgumentResolver appUserArgumentResolver;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(operatorUserInvitationController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        operatorUserInvitationController = (OperatorUserInvitationController) aopProxy.getProxy();
        mapper = new ObjectMapper();
        appUserArgumentResolver = Mockito.mock(AppUserArgumentResolver.class);
        validator = Mockito.mock(Validator.class);

        mockMvc = MockMvcBuilders.standaloneSetup(operatorUserInvitationController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(appUserArgumentResolver)
            .setValidator(validator)
            .build();
    }

    @Test
    void inviteOperatorUserToAccount() throws Exception {
        AppUser currentUser = AppUser.builder().userId("pmrv_user_id").roleType(RoleTypeConstants.OPERATOR).build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = buildMockOperatorUserInvitationDTO();
        Long accountId = 1L;

        when(appUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(appUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(currentUser);
        doNothing().when(operatorUserInvitationService).inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);

        mockMvc.perform(MockMvcRequestBuilders.post(OPERATOR_USER_CONTROLLER_REGISTRATION_BASE_PATH + ADD_TO_ACCOUNT_PATH + "/" + accountId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(operatorUserInvitationDTO)))
            .andExpect(status().isNoContent());

    }

    @Test
    void inviteOperatorUserToAccount_forbidden() throws Exception {
        AppUser currentUser = AppUser.builder().userId("pmrv_user_id").roleType(RoleTypeConstants.OPERATOR).build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = buildMockOperatorUserInvitationDTO();
        Long accountId = 1L;

        when(appUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(appUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(currentUser, "inviteOperatorUserToAccount", accountId.toString(), null, null);

        mockMvc.perform(MockMvcRequestBuilders.post(OPERATOR_USER_CONTROLLER_REGISTRATION_BASE_PATH + ADD_TO_ACCOUNT_PATH + "/" + accountId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(operatorUserInvitationDTO)))
            .andExpect(status().isForbidden());

    }

    private OperatorUserInvitationDTO buildMockOperatorUserInvitationDTO() {
        return OperatorUserInvitationDTO.builder()
            .email(OPERATOR_USER_EMAIL)
            .firstName(OPERATOR_USER_FNAME)
            .lastName(OPERATOR_USER_LNAME)
            .roleCode(OPERATOR)
            .build();
    }
}