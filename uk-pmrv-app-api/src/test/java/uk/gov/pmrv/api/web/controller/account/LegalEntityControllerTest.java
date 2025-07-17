package uk.gov.pmrv.api.web.controller.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.account.service.LegalEntityValidationService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LegalEntityControllerTest {

    public static final String LEGAL_ENTITY_CONTROLLER_PATH = "/v1.0/legal-entities";

    private MockMvc mockMvc;

    @InjectMocks
    private LegalEntityController legalEntityController;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    LegalEntityValidationService legalEntityValidationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private static final String USER_ID = "userId";
    private final String LEGAL_ENTITY_NAME = "name";

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(legalEntityController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        legalEntityController = (LegalEntityController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(legalEntityController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .build();
    }

    @Test
    void getCurrentUserLegalEntities() throws Exception {
    	List<LegalEntityInfoDTO> legalEntitiesInfoDTO = List.of(LegalEntityInfoDTO.builder().name(LEGAL_ENTITY_NAME).build());
    	AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(legalEntityService.getUserLegalEntities(appUser)).thenReturn(legalEntitiesInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(LEGAL_ENTITY_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(LEGAL_ENTITY_NAME));
        
        verify(legalEntityService, times(1)).getUserLegalEntities(appUser);
    }

    @Test
    void getLegalEntityById() throws Exception {
    	final Long id = 1L;
    	LegalEntityDTO legalEntity = LegalEntityDTO.builder().id(id).name(LEGAL_ENTITY_NAME).build();
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(legalEntityService.getUserLegalEntityDTOById(id, appUser)).thenReturn(legalEntity);

        mockMvc.perform(MockMvcRequestBuilders.get(LEGAL_ENTITY_CONTROLLER_PATH + "/" + id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").value(LEGAL_ENTITY_NAME))
            .andExpect(jsonPath("id").value(id));
        
        verify(legalEntityService, times(1)).getUserLegalEntityDTOById(id, appUser);
    }

    @Test
    void getLegalEntityById_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[] {RoleTypeConstants.OPERATOR, RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(LEGAL_ENTITY_CONTROLLER_PATH + "/" + 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(legalEntityService, never()).getUserLegalEntityDTOById(anyLong(), any());
    }

    @ParameterizedTest
    @MethodSource("provideLeNames")
    void isExistingLegalEntityName(String leName, boolean exists) throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(legalEntityValidationService.isExistingActiveLegalEntityName(leName, appUser)).thenReturn(exists);

        mockMvc.perform(MockMvcRequestBuilders.get(LEGAL_ENTITY_CONTROLLER_PATH + "/name")
                .param("name", leName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(exists)));

        verify(legalEntityValidationService, times(1)).isExistingActiveLegalEntityName(leName, appUser);
    }

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
                .userId(USER_ID)
                .build();
    }

    private static Stream<Arguments> provideLeNames() {
        return Stream.of(
                Arguments.of("lename", true),
                Arguments.of("lename2", false),
                Arguments.of("lename%25", true),
                Arguments.of("lename%2Fname", true),
                Arguments.of("lename%5Cname", true));
    }
}