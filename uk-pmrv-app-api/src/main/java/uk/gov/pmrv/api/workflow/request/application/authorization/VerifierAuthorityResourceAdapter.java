package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.rules.services.authorization.verifier.VerifierAccountAccessService;
import uk.gov.netz.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.VerifierAccountAccessByAccountTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VerifierAuthorityResourceAdapter 
    implements VerifierAccountAccessService, VerifierAccountAccessByAccountTypeService {
    
    private final VerifierAuthorityResourceService verifierAuthorityResourceService;
    private final AccountRepository accountRepository;
    private final RequestTaskRepository taskRepository;

    @Override
    public Set<Long> findAuthorizedAccountIds(final AppUser user) {
        return this.getUserScopedRequestTaskTypes(user).keySet();
    }
    
    @Override
    public Set<Long> findAuthorizedAccountIds(final AppUser user, final AccountType accountType) {
        return this.getUserScopedRequestTaskTypesByAccountType(user, accountType).keySet();
    }

    public Map<Long, Set<RequestTaskType>> getUserScopedRequestTaskTypesByAccountType(AppUser user, AccountType accountType) {

        final Map<Long, Set<RequestTaskType>> requestTaskTypesPerAccount = this.getUserScopedRequestTaskTypes(user);

        return requestTaskTypesPerAccount.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue()
                        .stream()
                        .filter(requestTaskType -> accountType.equals(requestTaskType.getRequestType().getAccountType()))
                        .collect(Collectors.toSet())
                )
            )
            .entrySet().stream()
            .filter(e -> !e.getValue().isEmpty())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Long, Set<RequestTaskType>> getUserScopedRequestTaskTypes(final AppUser user) {
        
        final Map<Long, Set<String>> requestTaskTypesPerVbId = 
            verifierAuthorityResourceService.findUserScopedRequestTaskTypes(user.getUserId());

        final boolean hasAccessToAllAccounts = this.hasUserPermissionToAccessAllAccounts(user);
        final Map<Long, Set<RequestTaskType>> requestTaskTypesPerAccount = new HashMap<>();

        for (final Map.Entry<Long, Set<String>> entry : requestTaskTypesPerVbId.entrySet()) {
            final Long vbId = entry.getKey();
            final Set<RequestTaskType> taskTypes = entry.getValue().stream().map(RequestTaskType::valueOf).collect(Collectors.toSet());
            final List<Long> accountIds = hasAccessToAllAccounts ?
                accountRepository.findAllIdsByVerificationBody(vbId) :
                taskRepository.findAccountIdsByTaskAssigneeAndTaskTypeInAndVerificationBody(user.getUserId(), taskTypes, vbId);
            accountIds.forEach(accId -> requestTaskTypesPerAccount.put(accId, taskTypes));
        }
        return requestTaskTypesPerAccount;
    }
    
    private boolean hasUserPermissionToAccessAllAccounts(final AppUser user) {

        return user.getAuthorities().stream()
            .filter(Objects::nonNull)
            .flatMap(pmrvAuthority -> pmrvAuthority.getPermissions().stream())
            .toList()
            .contains(Permission.PERM_VB_ACCESS_ALL_ACCOUNTS);
    }
}
