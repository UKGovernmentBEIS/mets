package uk.gov.pmrv.api.workflow.request.application.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityResourceAdapterTest {

    @InjectMocks
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Mock
    private VerifierAuthorityResourceService verifierAuthorityResourceService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RequestTaskRepository taskRepository;

    @Test
    void getUserScopedRequestTaskTypesByAccountTypeVerifierAdmin() {
        final String userId = "userId";
        final PmrvUser user = PmrvUser.builder().userId(userId)
            .authorities(List.of(PmrvAuthority.builder()
                .permissions(List.of(Permission.PERM_VB_ACCESS_ALL_ACCOUNTS)).build()))
            .build();
        final Long vbId = 1L;
        final AccountType accountType = AccountType.INSTALLATION;

        when(accountRepository.findAllIdsByVerificationBody(vbId)).thenReturn(List.of(3L, 4L));
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
            .thenReturn(Map.of(
                1L,
                Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT.name(),
                    RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name()
                )
            ));

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypesByAccountType =
            verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(user, accountType);

        assertThat(userScopedRequestTaskTypesByAccountType).containsExactlyInAnyOrderEntriesOf(
            Map.of(3L,
                Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT
                ),
                4L,
                Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT
                ))
        );
    }

    @Test
    void getUserScopedRequestTaskTypesByAccountTypeVerifier() {
        final String userId = "userId";
        final PmrvUser user = PmrvUser.builder().userId(userId)
            .authorities(List.of(PmrvAuthority.builder()
                .permissions(List.of()).build()))
            .build();
        final AccountType accountType = AccountType.INSTALLATION;

        final Long vbId = 1L;
        final List<Long> accountIds = List.of(2L, 3L);
        final Set<String> taskTypesString = Set.of(
            RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT.name(),
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT.name()
        );
        final Set<RequestTaskType> taskTypes = Set.of(
            RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT,
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT
        );
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(userId))
            .thenReturn(Map.of(
                vbId,
                taskTypesString
            ));
        when(taskRepository.findAccountIdsByTaskAssigneeAndTaskTypeInAndVerificationBody( "userId", taskTypes, vbId))
            .thenReturn(accountIds);

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypesByAccountType =
            verifierAuthorityResourceAdapter.getUserScopedRequestTaskTypesByAccountType(user, accountType);

        assertThat(userScopedRequestTaskTypesByAccountType).containsExactlyInAnyOrderEntriesOf(
            Map.of(3L,
                Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT
                ),
                2L,
                Set.of(
                    RequestTaskType.AER_APPLICATION_VERIFICATION_SUBMIT
                ))
        );
    }
}