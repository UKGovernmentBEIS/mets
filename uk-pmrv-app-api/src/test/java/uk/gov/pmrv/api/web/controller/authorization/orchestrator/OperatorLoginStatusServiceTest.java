package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class OperatorLoginStatusServiceTest {

    @InjectMocks
    private OperatorLoginStatusService operatorLoginStatusService;

    @Spy
    private ArrayList<OperatorAccountTypeLoginStatusService> operatorAccountTypeLoginStatusServices;

    @Mock
    private OperatorAviationLoginStatusService operatorAviationLoginStatusService;

    @Mock
    private OperatorInstallationLoginStatusService operatorInstallationLoginStatusService;

    @Mock
    private AuthorityRepository authorityRepository;


    @Test
    void getUserDomainsLoginStatusInfo() {
        operatorAccountTypeLoginStatusServices.add(operatorAviationLoginStatusService);
        operatorAccountTypeLoginStatusServices.add(operatorInstallationLoginStatusService);

        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(ACTIVE)
            .build();
        List<Authority> userAuthorities = List.of(authority);
        EnumMap<AccountType, LoginStatus> expectedLoginStatuses = new EnumMap<>(AccountType.class);
        expectedLoginStatuses.put(AccountType.INSTALLATION, LoginStatus.ENABLED);
        expectedLoginStatuses.put(AccountType.AVIATION, LoginStatus.DISABLED);

        when(authorityRepository.findByUserId(userId)).thenReturn(userAuthorities);
        when(operatorAviationLoginStatusService.getAccountType()).thenReturn(AccountType.AVIATION);
        when(operatorAviationLoginStatusService.getLoginStatus(userAuthorities)).thenReturn(LoginStatus.DISABLED);
        when(operatorInstallationLoginStatusService.getAccountType()).thenReturn(AccountType.INSTALLATION);
        when(operatorInstallationLoginStatusService.getLoginStatus(userAuthorities)).thenReturn(LoginStatus.ENABLED);

        UserDomainsLoginStatusInfo loginStatuses = operatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(loginStatuses.getDomainsLoginStatuses()).containsExactlyEntriesOf(expectedLoginStatuses);

        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(operatorAviationLoginStatusService, times(1)).getLoginStatus(userAuthorities);
        verify(operatorInstallationLoginStatusService, times(1)).getLoginStatus(userAuthorities);
    }

    @Test
    void getUserDomainsLoginStatusInfo_no_service_for_installation() {
        operatorAccountTypeLoginStatusServices.add(operatorAviationLoginStatusService);

        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(ACTIVE)
            .build();
        List<Authority> userAuthorities = List.of(authority);
        EnumMap<AccountType, LoginStatus> expectedLoginStatuses = new EnumMap<>(AccountType.class);
        expectedLoginStatuses.put(AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY);
        expectedLoginStatuses.put(AccountType.AVIATION, LoginStatus.ENABLED);

        when(authorityRepository.findByUserId(userId)).thenReturn(userAuthorities);
        when(operatorAviationLoginStatusService.getAccountType()).thenReturn(AccountType.AVIATION);
        when(operatorAviationLoginStatusService.getLoginStatus(userAuthorities)).thenReturn(LoginStatus.ENABLED);

        UserDomainsLoginStatusInfo loginStatuses = operatorLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(loginStatuses.getDomainsLoginStatuses()).containsExactlyEntriesOf(expectedLoginStatuses);

        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(operatorAviationLoginStatusService, times(1)).getLoginStatus(userAuthorities);
        verifyNoInteractions(operatorInstallationLoginStatusService);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleTypeConstants.OPERATOR, operatorLoginStatusService.getRoleType());
    }
}