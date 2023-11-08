package uk.gov.pmrv.api.web.controller.authorization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.UserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserStateDTO;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.EnumMap;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;

@ExtendWith(MockitoExtension.class)
class AuthorityControllerTest {

	private static final String BASE_PATH = "/v1.0/authorities";
	private static final String CURRENT_USER_STATE_PATH = "current-user-state";

	private MockMvc mockMvc;

	@InjectMocks
	private AuthorityController authorityController;

	@Mock
	private UserAuthorityQueryOrchestrator userAuthorityQueryOrchestrator;

	@Mock
	private PmrvSecurityComponent pmrvSecurityComponent;

	@BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorityController)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
	void getCurrentUserStatus() throws Exception {
		String userId = "userId";
		RoleType roleType = OPERATOR;
		AccountType lastLoginDomain = AccountType.INSTALLATION;
		PmrvUser currentUser = PmrvUser.builder().userId(userId).roleType(roleType).build();
		EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
		loginStatuses.put(AccountType.INSTALLATION, LoginStatus.ENABLED);
		loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_AUTHORITY);
		UserStateDTO userStatus = UserStateDTO.builder()
			.userId(userId)
			.roleType(roleType)
			.lastLoginDomain(lastLoginDomain)
			.domainsLoginStatuses(UserDomainsLoginStatusInfo.builder().domainsLoginStatuses(loginStatuses).build())
			.build();
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
		when(userAuthorityQueryOrchestrator.getUserState(currentUser)).thenReturn(userStatus);

		mockMvc.perform(MockMvcRequestBuilders
			.get(BASE_PATH + "/" + CURRENT_USER_STATE_PATH)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("userId").value(userId))
			.andExpect(jsonPath("roleType").value(String.valueOf(roleType)))
			.andExpect(jsonPath("lastLoginDomain").value(String.valueOf(lastLoginDomain)))
			.andExpect(jsonPath("$.domainsLoginStatuses",
				Matchers.hasEntry(AccountType.INSTALLATION.name(), LoginStatus.ENABLED.name())))
			.andExpect(jsonPath("$.domainsLoginStatuses",
				Matchers.hasEntry(AccountType.AVIATION.name(), LoginStatus.NO_AUTHORITY.name())));
	}
}
