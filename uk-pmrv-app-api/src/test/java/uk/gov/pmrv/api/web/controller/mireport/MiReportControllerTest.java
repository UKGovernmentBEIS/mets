package uk.gov.pmrv.api.web.controller.mireport;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.transform.StringToAccountTypeEnumConverter;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.common.MiReportService;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.pmrv.api.mireport.common.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.common.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
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
class MiReportControllerTest {

    private static final String MI_REPORT_BASE_CONTROLLER_PATH = "/v1.0/installation/mireports";
    private static final String REQUEST_TASK_TYPES_CONTROLLER_PATH = "/request-task-types";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportController miReportController;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private MiReportService miReportService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private OutstandingRequestTasksReportService outstandingRequestTasksReportService;

    private ObjectMapper objectMapper;

    private static final String USER_ID = "userId";
    private static final String ACCOUNT_ID = "emitterId";

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(miReportController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        miReportController = (MiReportController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(new StringToAccountTypeEnumConverter());

        mockMvc = MockMvcBuilders.standaloneSetup(miReportController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setConversionService(conversionService)
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserReports() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        List<MiReportSearchResult> searchResults = buildMockMiReports();
        PmrvUser pmrvUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(miReportService.findByCompetentAuthorityAndAccountType(pmrvUser.getCompetentAuthority(), accountType))
            .thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(searchResults.size()))
            .andExpect(jsonPath("$.[0].miReportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
        ;

        verify(miReportService, times(1))
            .findByCompetentAuthorityAndAccountType(pmrvUser.getCompetentAuthority(), accountType);
    }

    @Test
    void getCurrentUserReports_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.VERIFIER).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(miReportService);
    }

    @Test
    void getReportBy_LIST_OF_ACCOUNTS_USERS_CONTACTS() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        MiReportResult miReportResult = buildMockMiAccountsUsersContactsReport();
        AccountsUsersContactsMiReportResult
            accountsUsersContactsMiReport = (AccountsUsersContactsMiReportResult) miReportResult;
        AccountUserContact accountUserContact = accountsUsersContactsMiReport.getResults().get(0);
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(miReportService.generateReport(pmrvUser.getCompetentAuthority(), accountType, reportParams))
            .thenReturn(miReportResult);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
            .andExpect(jsonPath("$.results[0].Name").value(accountUserContact.getName()))
            .andExpect(jsonPath("$.results[0].Telephone").value(accountUserContact.getTelephone()))
            .andExpect(jsonPath("$.results[0].['Last logon']").value(accountUserContact.getLastLogon()))
            .andExpect(jsonPath("$.results[0].Email").value(accountUserContact.getEmail()))
            .andExpect(jsonPath("$.results[0].['User role']").value(accountUserContact.getRole()))
            .andExpect(jsonPath("$.results[0].['Account ID']").value(accountUserContact.getAccountId()))
            .andExpect(jsonPath("$.results[0].['Account name']").value(accountUserContact.getAccountName()))
            .andExpect(jsonPath("$.results[0].['Account status']").value(accountUserContact.getAccountStatus().toString()))
            .andExpect(jsonPath("$.results[0].['Account type']").value(accountUserContact.getAccountType().toString()))
            .andExpect(jsonPath("$.results[0].['User status']").value(accountUserContact.getAuthorityStatus().toString()))
            .andExpect(jsonPath("$.results[0].['Is User Financial contact?']").value(accountUserContact.getFinancialContact()))
            .andExpect(jsonPath("$.results[0].['Is User Primary contact?']").value(accountUserContact.getPrimaryContact()))
            .andExpect(jsonPath("$.results[0].['Is User Secondary contact?']").value(accountUserContact.getSecondaryContact()))
            .andExpect(jsonPath("$.results[0].['Is User Service contact?']").value(accountUserContact.getServiceContact()))
            .andExpect(jsonPath("$.results[0].['Legal Entity name']").value(accountUserContact.getLegalEntityName()))
            .andExpect(jsonPath("$.results[0].['Permit ID']").value(accountUserContact.getPermitId()))
            .andExpect(jsonPath("$.results[0].['Permit type/Account category']").value(accountUserContact.getPermitType()));

        verify(miReportService, times(1))
            .generateReport(pmrvUser.getCompetentAuthority(), accountType, reportParams);
    }

    @Test
    void getReport_not_found() throws Exception {
        AccountType accountType = AccountType.INSTALLATION;
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED))
            .when(miReportService).generateReport(pmrvUser.getCompetentAuthority(), accountType, reportParams);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isConflict());

        verify(miReportService, times(1))
            .generateReport(pmrvUser.getCompetentAuthority(), accountType, reportParams);
    }

    @Test
    void getReport_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(miReportService);
    }

    @Test
    void retrieveRegulatorRequestTaskTypes() throws Exception {
        PmrvUser pmrvUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(outstandingRequestTasksReportService.getRequestTaskTypesByRoleTypeAndAccountType(pmrvUser.getRoleType(), AccountType.INSTALLATION))
            .thenReturn(Set.of(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE));

        mockMvc.perform(MockMvcRequestBuilders
                .get(MI_REPORT_BASE_CONTROLLER_PATH + REQUEST_TASK_TYPES_CONTROLLER_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.toString(),
                RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE.toString())));

        verify(outstandingRequestTasksReportService, times(1))
            .getRequestTaskTypesByRoleTypeAndAccountType(pmrvUser.getRoleType(), AccountType.INSTALLATION);
    }

    @Test
    void retrieveRegulatorRequestTaskTypes_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockAuthenticatedUser();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .get(MI_REPORT_BASE_CONTROLLER_PATH + REQUEST_TASK_TYPES_CONTROLLER_PATH))
            .andExpect(status().isForbidden());

        verifyNoInteractions(outstandingRequestTasksReportService);
    }

    private MiReportResult buildMockMiAccountsUsersContactsReport() {
        AccountUserContact accountUserContact = AccountUserContact.builder()
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

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS)
            .results(List.of(accountUserContact))
            .build();
    }

    private PmrvUser buildMockAuthenticatedUser() {
        return PmrvUser.builder()
            .authorities(
                Arrays.asList(
                    PmrvAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                )
            )
            .roleType(RoleType.REGULATOR)
            .userId(USER_ID)
            .build();
    }

    private List<MiReportSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return Arrays.stream(MiReportType.values())
            .map(t -> MiReportEntity.builder().miReportType(t).competentAuthority(CompetentAuthorityEnum.ENGLAND))
            .map(e -> factory.createProjection(MiReportSearchResult.class, e))
            .collect(Collectors.toList());
    }

}