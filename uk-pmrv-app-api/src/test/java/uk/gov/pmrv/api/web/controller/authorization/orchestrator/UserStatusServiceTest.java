package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.ArrayList;
import java.util.EnumMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

    @InjectMocks
    private UserStatusService userStatusService;

    @Spy
    private ArrayList<UserRoleLoginStatusService> userRoleLoginStatusServices;

    @Mock
    private OperatorLoginStatusService operatorLoginStatusService;

    @BeforeEach
    void setUp() {
        userRoleLoginStatusServices.add(operatorLoginStatusService);
    }

    @Test
    void getLoginStatuses() {
        String userId = "userId";
        String roleType = RoleTypeConstants.OPERATOR;
        AppUser user = AppUser.builder().userId(userId).roleType(roleType).build();

        EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
        loginStatuses.put(AccountType.INSTALLATION, LoginStatus.ENABLED);
        loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_AUTHORITY);
        UserDomainsLoginStatusInfo expectedUserDomainsLoginStatusInfo = UserDomainsLoginStatusInfo.builder()
            .domainsLoginStatuses(loginStatuses)
            .build();

        when(operatorLoginStatusService.getRoleType()).thenReturn(roleType);
        when(operatorLoginStatusService.getUserDomainsLoginStatusInfo(userId)).thenReturn(expectedUserDomainsLoginStatusInfo);

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = userStatusService.getUserDomainsLoginStatusInfo(user);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyEntriesOf(loginStatuses);

        verify(operatorLoginStatusService, times(1)).getUserDomainsLoginStatusInfo(userId);
        verify(operatorLoginStatusService, times(1)).getRoleType();
    }

    @Test
    void getLoginStatuses_no_role_type_suitable_Service() {
        String userId = "userId";
        String roleType = RoleTypeConstants.REGULATOR;
        AppUser user = AppUser.builder().userId(userId).roleType(roleType).build();

        EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
        loginStatuses.put(AccountType.INSTALLATION, LoginStatus.NO_ROLE_TYPE);
        loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_ROLE_TYPE);

        when(operatorLoginStatusService.getRoleType()).thenReturn(roleType);

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = userStatusService.getUserDomainsLoginStatusInfo(user);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyEntriesOf(loginStatuses);

        verify(operatorLoginStatusService, times(1)).getRoleType();
    }
}