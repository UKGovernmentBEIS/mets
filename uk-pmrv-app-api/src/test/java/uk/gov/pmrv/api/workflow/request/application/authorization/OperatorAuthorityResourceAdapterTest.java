package uk.gov.pmrv.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.rules.services.resource.OperatorAuthorityResourceService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityResourceAdapterTest {

    @InjectMocks
    private OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Mock
    private OperatorAuthorityResourceService operatorAuthorityResourceService;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void getUserScopedRequestTaskTypesByAccountId() {
        final String userId = "userId";
        final Long accountId = 1L;

        when(operatorAuthorityResourceService.findUserScopedRequestTaskTypesByAccounts(userId, Set.of(accountId)))
            .thenReturn(Map.of(
                accountId, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name(), RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE.name()))
            );

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypesByAccounts =
            operatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountId(userId, accountId);

        assertThat(userScopedRequestTaskTypesByAccounts).containsExactlyEntriesOf(Map.of(
            accountId,
            Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT, RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE))
        );

        verify(operatorAuthorityResourceService, times(1))
            .findUserScopedRequestTaskTypesByAccounts(userId, Set.of(accountId));
        verifyNoInteractions(accountQueryService);
    }

    @Test
    void getUserScopedRequestTaskTypesByAccountType() {
        final Long accountId1 = 1L;
        final Long accountId2 = 2L;
        final List<Long> accounts = List.of(accountId1, accountId2);
        final String userId = "userId";
        final PmrvUser pmrvUser = PmrvUser.builder()
            .userId(userId)
            .authorities(List.of(
                PmrvAuthority.builder().accountId(accountId1).build(),
                PmrvAuthority.builder().accountId(accountId2).build()
                )
            )
            .build();
        final AccountType accountType = AccountType.INSTALLATION;
        final Set<Long> installationAccounts = Set.of(accountId1, accountId2);

        when(accountQueryService.getAccountIdsByAccountType(accounts, accountType)).thenReturn(installationAccounts);
        when(operatorAuthorityResourceService.findUserScopedRequestTaskTypesByAccounts(userId, installationAccounts))
            .thenReturn(Map.of(
                accountId1, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name())
                )
            );

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypesByAccountType =
            operatorAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(pmrvUser, accountType);

        assertThat(userScopedRequestTaskTypesByAccountType).containsExactlyEntriesOf(
            Map.of(accountId1, Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT))
        );

        verify(accountQueryService, times(1)).getAccountIdsByAccountType(accounts, accountType);
        verify(operatorAuthorityResourceService, times(1))
            .findUserScopedRequestTaskTypesByAccounts(userId, installationAccounts);
    }
}