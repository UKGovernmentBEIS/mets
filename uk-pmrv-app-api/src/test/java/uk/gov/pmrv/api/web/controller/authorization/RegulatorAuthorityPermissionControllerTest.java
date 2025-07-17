package uk.gov.pmrv.api.web.controller.authorization;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.regulator.domain.AuthorityManagePermissionDTO;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.netz.api.authorization.regulator.transform.RegulatorPermissionsAdapter;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.VIEW_ONLY;
import static uk.gov.pmrv.api.authorization.regulator.domain.PmrvRegulatorPermissionGroup.REVIEW_INSTALLATION_ACCOUNT;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityPermissionControllerTest {

    private static final String BASE_PATH = "/v1.0/regulator-authorities/permissions";
    private static final String USER_ID = "userId";

    private MockMvc mockMvc;

    @InjectMocks
    private RegulatorAuthorityPermissionController regulatorAuthorityPermissionController;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RegulatorAuthorityQueryService regulatorAuthorityQueryService;
    
    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private RegulatorPermissionsAdapter regulatorPermissionsAdapter;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(regulatorAuthorityPermissionController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        regulatorAuthorityPermissionController = (RegulatorAuthorityPermissionController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(regulatorAuthorityPermissionController)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getCurrentRegulatorUserPermissions() throws Exception {
        AppUser currentUser = buildMockRegulatorUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(regulatorAuthorityQueryService.getCurrentRegulatorUserPermissions(currentUser))
            .thenReturn(AuthorityManagePermissionDTO.builder()
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE))
                .editable(false)
                .build());

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("editable").value(false))
            .andExpect(jsonPath("$.permissions", Matchers.hasKey(MANAGE_USERS_AND_CONTACTS)))
            .andExpect(jsonPath("$.permissions", Matchers.hasValue(RegulatorPermissionLevel.NONE.name())));

        verify(regulatorAuthorityQueryService, times(1)).getCurrentRegulatorUserPermissions(currentUser);
    }

    @Test
    void getCurrentRegulatorUserPermission_forbidden() throws Exception {
        AppUser currentUser = buildMockRegulatorUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(currentUser, new String[] {RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(regulatorAuthorityQueryService, never()).getCurrentRegulatorUserPermissions(any());
    }

    @Test
    void getRegulatorUserPermissionsByCaAndId() throws Exception {
        AppUser currentUser = buildMockRegulatorUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(regulatorAuthorityQueryService.getRegulatorUserPermissionsByUserId(currentUser, USER_ID))
            .thenReturn(AuthorityManagePermissionDTO.builder()
                .permissions(Map.of(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE))
                .editable(true)
                .build());

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.editable").value(true))
            .andExpect(jsonPath("$.permissions", Matchers.hasKey(MANAGE_USERS_AND_CONTACTS)))
            .andExpect(jsonPath("$.permissions", Matchers.hasValue(RegulatorPermissionLevel.NONE.name())));

        verify(regulatorAuthorityQueryService, times(1))
            .getRegulatorUserPermissionsByUserId(currentUser, USER_ID);
    }

    @Test
    void getRegulatorUserPermissionsByCaAndId_forbidden() throws Exception {
        AppUser currentUser = buildMockRegulatorUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(currentUser, "getRegulatorUserPermissionsByCaAndId");

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(regulatorAuthorityQueryService, never()).getRegulatorUserPermissionsByUserId(any(), anyString());
    }

    @Test
    void getRegulatorPermissionGroupLevels() throws Exception {
        when(regulatorPermissionsAdapter.getPermissionGroupLevels()).thenReturn(Map.of(REVIEW_INSTALLATION_ACCOUNT, List.of(NONE, VIEW_ONLY, EXECUTE)));

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/group-levels")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.REVIEW_INSTALLATION_ACCOUNT.[0]").value("NONE"))
            .andExpect(jsonPath("$.REVIEW_INSTALLATION_ACCOUNT.[1]").value("VIEW_ONLY"))
            .andExpect(jsonPath("$.REVIEW_INSTALLATION_ACCOUNT.[2]").value("EXECUTE"));

        verify(regulatorPermissionsAdapter, times(1)).getPermissionGroupLevels();
    }

    @Test
    void getRegulatorPermissionGroupLevels_forbidden() throws Exception {

        AppUser currentUser = buildMockRegulatorUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(currentUser, "getRegulatorPermissionGroupLevels");

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/group-levels")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    private AppUser buildMockRegulatorUser() {
        return AppUser.builder()
            .userId(USER_ID)
            .roleType(RoleTypeConstants.REGULATOR)
            .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
            .build();
    }

}