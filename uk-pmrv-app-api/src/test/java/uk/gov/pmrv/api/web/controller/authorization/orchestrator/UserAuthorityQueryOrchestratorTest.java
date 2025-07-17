package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserStateDTO;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAuthorityQueryOrchestratorTest {

    @InjectMocks
    private UserAuthorityQueryOrchestrator orchestrator;

    @Mock
    private UserStatusService userStatusService;

    @Mock
    private UserLoginDomainService userLoginDomainService;

    @Test
    void getUserState() {
        String userId = "userId";
        String roleType = RoleTypeConstants.REGULATOR;
        AppUser user = AppUser.builder()
            .userId(userId)
            .roleType(roleType)
            .build();

        EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
        loginStatuses.put(AccountType.INSTALLATION, LoginStatus.ENABLED);
        loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_AUTHORITY);
        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = UserDomainsLoginStatusInfo.builder().domainsLoginStatuses(loginStatuses).build();

        when(userLoginDomainService.getUserLastLoginDomain(userId)).thenReturn(AccountType.INSTALLATION);
        when(userStatusService.getUserDomainsLoginStatusInfo(user)).thenReturn(userDomainsLoginStatusInfo);

        UserStateDTO expectedUserState = UserStateDTO.builder()
            .userId(userId)
            .roleType(roleType)
            .lastLoginDomain(AccountType.INSTALLATION)
            .domainsLoginStatuses(userDomainsLoginStatusInfo)
            .build();

        assertEquals(expectedUserState, orchestrator.getUserState(user));

        verify(userLoginDomainService, times(1)).getUserLastLoginDomain(userId);
        verify(userStatusService, times(1)).getUserDomainsLoginStatusInfo(user);
    }

}