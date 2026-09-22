package uk.gov.pmrv.api.web.controller.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
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
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.SettingsSection;
import uk.gov.pmrv.api.settings.domain.dto.FeeHistoryEntryDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeHistoryResponseDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeRowDTO;
import uk.gov.pmrv.api.settings.domain.dto.FeeUpdateDTO;
import uk.gov.pmrv.api.settings.domain.enumeration.FeeHistoryActionType;
import uk.gov.pmrv.api.settings.service.FeeHistoryService;
import uk.gov.pmrv.api.settings.service.SettingsFeeService;
import uk.gov.pmrv.api.settings.service.SettingsService;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/%s/settings";

    @InjectMocks
    private SettingsController controller;

    @Mock
    private SettingsService settingsService;

    @Mock
    private SettingsFeeService settingsFeeService;

    @Mock
    private FeeHistoryService feeHistoryService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect authorizedAspect =
                new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect =
                new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedAspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (SettingsController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .build();
    }

    @Test
    void getAccessibleSections_returnsSections() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.REGULATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        List<SettingsSection> sections = List.of(SettingsSection.values());
        when(settingsService.getAccessibleSections(any(), any())).thenReturn(sections);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH, AccountType.INSTALLATION.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(sections.size()));

        verify(settingsService, times(1)).getAccessibleSections(any(), any());
    }

    @Test
    void getAccessibleSections_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.OPERATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH, AccountType.INSTALLATION.name())))
                .andExpect(status().isForbidden());

        verifyNoInteractions(settingsService);
    }

    @Test
    void getFees_returnsFeeRows() throws Exception {
        AppAuthority authority = AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build();
        AppUser appUser = AppUser.builder()
                .userId("user1")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(authority))
                .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        List<FeeRowDTO> fees = List.of(
                FeeRowDTO.builder().requestType(RequestType.PERMIT_ISSUANCE).feeType(FeeType.HSE).amount(new BigDecimal("1398")).build(),
                FeeRowDTO.builder().requestType(RequestType.PERMIT_SURRENDER).feeType(FeeType.FIXED).amount(new BigDecimal("1452")).build()
        );
        when(settingsFeeService.getFees(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION)).thenReturn(fees);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH + "/fees", AccountType.INSTALLATION.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].requestType").value("PERMIT_ISSUANCE"))
                .andExpect(jsonPath("$[0].feeType").value("HSE"))
                .andExpect(jsonPath("$[0].amount").value(1398));

        verify(settingsFeeService, times(1)).getFees(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION);
    }

    @Test
    void getFees_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.REGULATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getFees");

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH + "/fees", AccountType.INSTALLATION.name())))
                .andExpect(status().isForbidden());

        verifyNoInteractions(settingsFeeService);
    }

    @Test
    void getFeeHistory_returnsPagedHistory() throws Exception {
        AppAuthority authority = AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build();
        AppUser appUser = AppUser.builder()
                .userId("user1")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(authority))
                .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        FeeHistoryResponseDTO response = FeeHistoryResponseDTO.builder()
                .history(List.of(FeeHistoryEntryDTO.builder()
                        .requestType(RequestType.PERMIT_ISSUANCE)
                        .feeType(FeeType.HSE)
                        .actionType(FeeHistoryActionType.IMMEDIATE_UPDATE)
                        .changedBy("John Doe")
                        .oldAmount(new BigDecimal("1000"))
                        .newAmount(new BigDecimal("1500"))
                        .build()))
                .totalItems(1L)
                .build();
        when(feeHistoryService.getHistory(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 0, 30))
                .thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH + "/fees/history", AccountType.INSTALLATION.name()))
                        .param("page", "0")
                        .param("size", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.history").isArray())
                .andExpect(jsonPath("$.history.length()").value(1))
                .andExpect(jsonPath("$.history[0].changedBy").value("John Doe"))
                .andExpect(jsonPath("$.history[0].requestType").value("PERMIT_ISSUANCE"));

        verify(feeHistoryService, times(1)).getHistory(CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 0, 30);
    }

    @Test
    void getFeeHistory_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.REGULATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getFeeHistory");

        mockMvc.perform(MockMvcRequestBuilders.get(String.format(CONTROLLER_PATH + "/fees/history", AccountType.INSTALLATION.name()))
                        .param("page", "0")
                        .param("size", "30"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(feeHistoryService);
    }

    @Test
    void updateFee_updatesAmount() throws Exception {
        AppAuthority authority = AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build();
        AppUser appUser = AppUser.builder()
                .userId("user1")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(authority))
                .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        FeeUpdateDTO dto = FeeUpdateDTO.builder()
                .amount(new BigDecimal("1500"))
                .effectiveDate(LocalDate.now())
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put(String.format(CONTROLLER_PATH + "/fees/1/FIXED", AccountType.INSTALLATION.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(settingsFeeService, times(1)).updateFee(
                eq(CompetentAuthorityEnum.ENGLAND), eq(AccountType.INSTALLATION), eq(1L), eq(FeeType.FIXED),
                any(FeeUpdateDTO.class), eq(appUser));
    }

    @Test
    void updateFee_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.REGULATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "updateFee");

        FeeUpdateDTO dto = FeeUpdateDTO.builder()
                .amount(new BigDecimal("1500"))
                .effectiveDate(LocalDate.now())
                .build();

        mockMvc.perform(MockMvcRequestBuilders
                        .put(String.format(CONTROLLER_PATH + "/fees/1/FIXED", AccountType.INSTALLATION.name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(settingsFeeService);
    }

    @Test
    void cancelScheduledFeeUpdate_cancelsScheduledChange() throws Exception {
        AppAuthority authority = AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build();
        AppUser appUser = AppUser.builder()
                .userId("user1")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(authority))
                .build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format(CONTROLLER_PATH + "/fees/1/FIXED/scheduled-change", AccountType.INSTALLATION.name())))
                .andExpect(status().isOk());

        verify(settingsFeeService, times(1)).cancelScheduledFeeUpdate(
                CompetentAuthorityEnum.ENGLAND, AccountType.INSTALLATION, 1L, FeeType.FIXED, appUser);
    }

    @Test
    void cancelScheduledFeeUpdate_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().userId("user1").roleType(RoleTypeConstants.REGULATOR).build();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "cancelScheduledFeeUpdate");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete(String.format(CONTROLLER_PATH + "/fees/1/FIXED/scheduled-change", AccountType.INSTALLATION.name())))
                .andExpect(status().isForbidden());

        verifyNoInteractions(settingsFeeService);
    }
}
