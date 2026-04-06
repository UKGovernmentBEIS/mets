package uk.gov.pmrv.api.web.controller.mireport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.system.MiReportSystemType;
import uk.gov.netz.api.mireport.system.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemEntity;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemResultTypesProvider;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.aviation.outstandingrequesttasks.AviationOutstandingRequestTasksReportService;
import uk.gov.pmrv.api.mireport.system.common.PmrvMiReportSystemService;
import uk.gov.pmrv.api.mireport.system.common.jsonprovider.PmrvMiReportParamsTypesProvider;
import uk.gov.pmrv.api.mireport.system.common.jsonprovider.PmrvMiReportResultTypesProvider;
import uk.gov.pmrv.api.mireport.system.installation.accountuserscontacts.InstallationAccountUserContact;
import uk.gov.pmrv.api.mireport.system.installation.outstandingrequesttasks.InstallationOutstandingRequestTasksReportService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.web.config.AppUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MiReportSystemControllerTest {

    private static final String MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION = "/v1.0/installation/mireports/system";
    private static final String MI_REPORT_BASE_CONTROLLER_PATH_AVIATION = "/v1.0/aviation/mireports/system";
    private static final String REQUEST_TASK_TYPES_CONTROLLER_PATH = "/request-task-types";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportSystemController miReportSystemController;

    @Mock
    private AppSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvMiReportSystemService pmrvMiReportSystemService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AviationOutstandingRequestTasksReportService aviationOutstandingRequestTasksReportService;

    @Mock
    private InstallationOutstandingRequestTasksReportService installationOutstandingRequestTasksReportService;

    private ObjectMapper objectMapper;

    private static final String USER_ID = "userId";
    private static final String ACCOUNT_ID = "emitterId";

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(new MiReportSystemParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new MiReportSystemResultTypesProvider().getTypes().toArray(NamedType[]::new));

        objectMapper.registerSubtypes(new PmrvMiReportParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new PmrvMiReportResultTypesProvider().getTypes().toArray(NamedType[]::new));

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
                authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(miReportSystemController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        miReportSystemController = (MiReportSystemController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(miReportSystemController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(new AppUserArgumentResolver(pmrvSecurityComponent))
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .addFilters(new FilterChainProxy(Collections.emptyList()))
                .setConversionService(conversionService)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserReports() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        List<MiReportSystemSearchResult> searchResults = buildMockMiReports();
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(pmrvMiReportSystemService.findByCompetentAuthorityAndAccountType(appUser.getCompetentAuthority(), accountType))
                .thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION + "/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(searchResults.size()))
                .andExpect(jsonPath("$.[0].miReportType").value(searchResults.getFirst().getMiReportType()))
        ;

        verify(pmrvMiReportSystemService, times(1))
                .findByCompetentAuthorityAndAccountType(appUser.getCompetentAuthority(), accountType);
    }

    @Test
    void getCurrentUserReports_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION + "/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pmrvMiReportSystemService);
    }

    @Test
    void generateReport_LIST_OF_ACCOUNTS_USERS_CONTACTS() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        AccountsUsersContactsMiReportResult<InstallationAccountUserContact>
                accountsUsersContactsMiReport = buildMockMiAccountsUsersContactsReport();
        InstallationAccountUserContact accountUserContact = accountsUsersContactsMiReport.getResults().getFirst();
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(pmrvMiReportSystemService.generateReport(appUser.getCompetentAuthority(), accountType, reportParams))
                .thenReturn(accountsUsersContactsMiReport);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportType").value(MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS))
                .andExpect(jsonPath("$.results[0].Name").value(accountUserContact.getName()))
                .andExpect(jsonPath("$.results[0].Telephone").value(accountUserContact.getTelephone()))
                .andExpect(jsonPath("$.results[0].['Last logon']").value(accountUserContact.getLastLogon()))
                .andExpect(jsonPath("$.results[0].Email").value(accountUserContact.getEmail()))
                .andExpect(jsonPath("$.results[0].['User role']").value(accountUserContact.getRole()))
                .andExpect(jsonPath("$.results[0].['Account ID']").value(accountUserContact.getAccountId()))
                .andExpect(jsonPath("$.results[0].['Account name']").value(accountUserContact.getAccountName()))
                .andExpect(jsonPath("$.results[0].['Account status']").value(accountUserContact.getAccountStatus()))
                .andExpect(jsonPath("$.results[0].['Account type']").value(accountUserContact.getAccountType()))
                .andExpect(jsonPath("$.results[0].['User status']").value(accountUserContact.getAuthorityStatus()))
                .andExpect(jsonPath("$.results[0].['Is User Financial contact?']").value(accountUserContact.getFinancialContact()))
                .andExpect(jsonPath("$.results[0].['Is User Primary contact?']").value(accountUserContact.getPrimaryContact()))
                .andExpect(jsonPath("$.results[0].['Is User Secondary contact?']").value(accountUserContact.getSecondaryContact()))
                .andExpect(jsonPath("$.results[0].['Is User Service contact?']").value(accountUserContact.getServiceContact()))
                .andExpect(jsonPath("$.results[0].['Legal Entity name']").value(accountUserContact.getLegalEntityName()))
                .andExpect(jsonPath("$.results[0].['Permit ID']").value(accountUserContact.getPermitId()))
                .andExpect(jsonPath("$.results[0].['Permit type/Account category']").value(accountUserContact.getPermitType()));

        verify(pmrvMiReportSystemService, times(1))
                .generateReport(appUser.getCompetentAuthority(), accountType, reportParams);
    }

    @Test
    void generateReport_not_found() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED))
                .when(pmrvMiReportSystemService).generateReport(appUser.getCompetentAuthority(), accountType, reportParams);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportParams)))
                .andExpect(status().isConflict());

        verify(pmrvMiReportSystemService, times(1))
                .generateReport(appUser.getCompetentAuthority(), accountType, reportParams);
    }

    @Test
    void generateReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                        .post(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportParams)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(pmrvMiReportSystemService);
    }

    @Test
    void getRegulatorRequestTaskTypes_installation() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(installationOutstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), AccountType.INSTALLATION))
                .thenReturn(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION + REQUEST_TASK_TYPES_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.toString(),
                        RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE.toString())));

        verify(installationOutstandingRequestTasksReportService, times(1))
                .getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), AccountType.INSTALLATION);
    }

    @Test
    void getRegulatorRequestTaskTypes_aviation() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(aviationOutstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), AccountType.AVIATION))
                .thenReturn(Set.of(RequestTaskType.AVIATION_VIR_WAIT_FOR_RFI_RESPONSE, RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(MI_REPORT_BASE_CONTROLLER_PATH_AVIATION + REQUEST_TASK_TYPES_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(RequestTaskType.AVIATION_VIR_WAIT_FOR_RFI_RESPONSE.toString(),
                        RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW.toString())));

        verify(aviationOutstandingRequestTasksReportService, times(1))
                .getRequestTaskTypesByRoleTypeAndAccountType(appUser.getRoleType(), AccountType.AVIATION);
    }

    @Test
    void getRegulatorRequestTaskTypes_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(roleAuthorizationService)
                .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                        .get(MI_REPORT_BASE_CONTROLLER_PATH_INSTALLATION + REQUEST_TASK_TYPES_CONTROLLER_PATH)
                        .param("accountType", AccountType.INSTALLATION.name()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(installationOutstandingRequestTasksReportService);
    }

    private AccountsUsersContactsMiReportResult<InstallationAccountUserContact> buildMockMiAccountsUsersContactsReport() {
        InstallationAccountUserContact accountUserContact = InstallationAccountUserContact.builder()
                .name("Foo Bar")
                .telephone("")
                .lastLogon("")
                .email("test@test.com")
                .role("Operator")
                .email(ACCOUNT_ID)
                .accountName("account name")
                .accountStatus(InstallationAccountStatus.LIVE.name())
                .accountType(AccountType.INSTALLATION.name())
                .authorityStatus(AuthorityStatus.ACTIVE.name())
                .financialContact(Boolean.TRUE)
                .primaryContact(Boolean.TRUE)
                .secondaryContact(Boolean.FALSE)
                .serviceContact(Boolean.FALSE)
                .legalEntityName("Legal")
                .permitId("Permit id 1")
                .permitType(PermitType.GHGE.toString())
                .build();

        return AccountsUsersContactsMiReportResult.<InstallationAccountUserContact>builder()
                .reportType(MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS)
                .results(List.of(accountUserContact))
                .build();
    }

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
                .authorities(
                        Arrays.asList(
                                AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                        )
                )
                .roleType(RoleTypeConstants.REGULATOR)
                .userId(USER_ID)
                .build();
    }

    private List<MiReportSystemSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        Set<String> reportNames = Set.of(MiReportSystemType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS,
                MiReportSystemType.REGULATOR_OUTSTANDING_REQUEST_TASKS,
                MiReportSystemType.COMPLETED_WORK,
                MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS);

        return reportNames.stream()
                .map(t -> MiReportSystemEntity.builder().miReportType(t).competentAuthority(CompetentAuthorityEnum.ENGLAND))
                .map(e -> factory.createProjection(MiReportSystemSearchResult.class, e))
                .collect(Collectors.toList());
    }

}