package uk.gov.pmrv.api.web.controller.account.installation;

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
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.dto.PagingRequest;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

@ExtendWith(MockitoExtension.class)
class InstallationAccountControllerTest {
	private static final String ACCOUNT_CONTROLLER_PATH = "/v1.0/installation/accounts";

	private MockMvc mockMvc;

	@InjectMocks
	private InstallationAccountController installationAccountController;

	@Mock
    private AccountQueryService accountQueryService;

	@Mock
	private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(installationAccountController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        installationAccountController = (InstallationAccountController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(installationAccountController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .build()
        ;
    }

	@Test
	void getCurrentUserAccounts() throws Exception {
		final PmrvUser user = PmrvUser.builder().userId("userId").build();
		final AccountSearchCriteria criteria = AccountSearchCriteria.builder()
                    .term("key")
                    .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();
		
		final List<AccountSearchResultsInfoDTO> accounts =
				List.of(
				        new AccountSearchResultsInfoDTO(1L, "account1", "EM00009", InstallationAccountStatus.LIVE.name(), "lename1"),
                        new AccountSearchResultsInfoDTO(2L, "account2", "EM00010", InstallationAccountStatus.LIVE.name(), "lename2"));
		final AccountSearchResults results = AccountSearchResults.builder().accounts(accounts).total(2L).build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(installationAccountQueryService.getAccountsByUserAndSearchCriteria(user, criteria)).thenReturn(results);

		mockMvc.perform(MockMvcRequestBuilders
		            .get(ACCOUNT_CONTROLLER_PATH)
    		        .param("term", criteria.getTerm())
    		        .param("page", String.valueOf(criteria.getPaging().getPageNumber()))
    		        .param("size", String.valueOf(criteria.getPaging().getPageSize()))
    				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accounts[0].id").value(1L))
				.andExpect(jsonPath("$.accounts[0].name").value("account1"))
				.andExpect(jsonPath("$.accounts[0].emitterId").value("EM00009"))
				.andExpect(jsonPath("$.accounts[1].id").value(2L))
				.andExpect(jsonPath("$.accounts[1].name").value("account2"))
				.andExpect(jsonPath("$.accounts[1].emitterId").value("EM00010"))
				;

		verify(installationAccountQueryService, times(1)).getAccountsByUserAndSearchCriteria(user, criteria);
	}

    @Test
    void getCurrentUserAccounts_forbidden() throws Exception {
        final PmrvUser user = PmrvUser.builder().userId("userId").build();
        final AccountSearchCriteria criteria = AccountSearchCriteria.builder()
                .term("key")
                .paging(PagingRequest.builder().pageNumber(0L).pageSize(10L).build()).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(user, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
                        .get(ACCOUNT_CONTROLLER_PATH)
                        .param("term", criteria.getTerm())
                        .param("page", String.valueOf(criteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(criteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(installationAccountQueryService, never()).getAccountsByUserAndSearchCriteria(user, criteria);
    }

	@ParameterizedTest
	@MethodSource("provideAccountNames")
	void isExistingLegalEntityName(String accountName, boolean exists) throws Exception {

		when(accountQueryService.isExistingActiveAccountName(accountName, null)).thenReturn(exists);

		mockMvc.perform(MockMvcRequestBuilders.get(ACCOUNT_CONTROLLER_PATH + "/name")
				.param("name", accountName)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(exists)));

		verify(accountQueryService, times(1)).isExistingActiveAccountName(accountName, null);
	}

	@ParameterizedTest
	@MethodSource("provideAccountNamesAndAccountIds")
	void isExistingAcountNameWithAccountId(String accountName, boolean exists, Long accountId) throws Exception {

		when(accountQueryService.isExistingActiveAccountName(accountName, accountId)).thenReturn(exists);

		mockMvc.perform(MockMvcRequestBuilders.get(ACCOUNT_CONTROLLER_PATH + "/name")
						.param("name", accountName)
						.param("accountId", String.valueOf(accountId))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(String.valueOf(exists)));

		verify(accountQueryService, times(1)).isExistingActiveAccountName(accountName, accountId);
	}

	private static Stream<Arguments> provideAccountNames() {
		return Stream.of(
				Arguments.of("account", true),
				Arguments.of("account2", false),
				Arguments.of("account%25", true),
				Arguments.of("account%2Fname", true),
				Arguments.of("account%5Cname", true));
	}

	private static Stream<Arguments> provideAccountNamesAndAccountIds() {
		return Stream.of(
				Arguments.of("account", true, 1L),
				Arguments.of("account2", false, 1L),
				Arguments.of("account%25", true, 1L),
				Arguments.of("account%2Fname", true, 1L),
				Arguments.of("account%5Cname", true, 1L));
	}
}
