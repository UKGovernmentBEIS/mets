package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorAviationLoginStatusServiceTest {

    @InjectMocks
    private OperatorAviationLoginStatusService service;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void getLoginStatus_no_authorities_for_aviation_then_no_authority() {
        String userId = "userId";
        Long accountId = 1L;
        AccountType accountType = AccountType.AVIATION;
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(AuthorityStatus.ACTIVE)
            .build();
        List<Authority> userAuthorities = List.of(authority);

        when(accountQueryService.getAccountIdsByAccountType(List.of(accountId), accountType)).thenReturn(Set.of());

        assertEquals(LoginStatus.NO_AUTHORITY, service.getLoginStatus(userAuthorities));

        verify(accountQueryService, times(1))
            .getAccountIdsByAccountType(List.of(accountId), accountType);
    }

    @Test
    void getLoginStatus_when_active_authorities_with_permissions_then_enabled() {
        String userId = "userId";
        Long accountId = 1L;
        AccountType accountType = AccountType.AVIATION;
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(AuthorityStatus.ACTIVE)
            .build();
        authority.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));
        List<Authority> userAuthorities = List.of(authority);

        when(accountQueryService.getAccountIdsByAccountType(List.of(accountId), accountType))
            .thenReturn(Set.of(accountId));

        assertEquals(LoginStatus.ENABLED, service.getLoginStatus(userAuthorities));

        verify(accountQueryService, times(1))
            .getAccountIdsByAccountType(List.of(accountId), accountType);
    }

    @Test
    void getLoginStatus_when_active_authorities_without_permissions_then_no_authority() {
        String userId = "userId";
        Long accountId = 1L;
        AccountType accountType = AccountType.AVIATION;
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(AuthorityStatus.ACTIVE)
            .build();
        List<Authority> userAuthorities = List.of(authority);

        when(accountQueryService.getAccountIdsByAccountType(List.of(accountId), accountType))
            .thenReturn(Set.of(accountId));

        assertEquals(LoginStatus.NO_AUTHORITY, service.getLoginStatus(userAuthorities));

        verify(accountQueryService, times(1))
            .getAccountIdsByAccountType(List.of(accountId), accountType);
    }

    @Test
    void getLoginStatus_when_no_active_authorities_but_accepted_then_accepted() {
        String userId = "userId";
        Long accountId = 1L;
        AccountType accountType = AccountType.AVIATION;
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(AuthorityStatus.ACCEPTED)
            .build();
        List<Authority> userAuthorities = List.of(authority);

        when(accountQueryService.getAccountIdsByAccountType(List.of(accountId), accountType))
            .thenReturn(Set.of(accountId));

        assertEquals(LoginStatus.ACCEPTED, service.getLoginStatus(userAuthorities));

        verify(accountQueryService, times(1))
            .getAccountIdsByAccountType(List.of(accountId), accountType);
    }

    @Test
    void getLoginStatus_when_nor_active_authorities_nor_accepted_then_disabled() {
        String userId = "userId";
        Long accountId = 1L;
        AccountType accountType = AccountType.AVIATION;
        Authority authority = Authority.builder()
            .userId(userId)
            .accountId(1L)
            .status(AuthorityStatus.PENDING)
            .build();
        List<Authority> userAuthorities = List.of(authority);

        when(accountQueryService.getAccountIdsByAccountType(List.of(accountId), accountType))
            .thenReturn(Set.of(accountId));

        assertEquals(LoginStatus.DISABLED, service.getLoginStatus(userAuthorities));

        verify(accountQueryService, times(1))
            .getAccountIdsByAccountType(List.of(accountId), accountType);
    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, service.getAccountType());
    }
}