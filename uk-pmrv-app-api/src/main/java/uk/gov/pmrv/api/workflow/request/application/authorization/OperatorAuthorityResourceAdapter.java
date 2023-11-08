package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.rules.services.resource.OperatorAuthorityResourceService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//TODO: This could be implemented with an interface per user role type
@Service
@RequiredArgsConstructor
public class OperatorAuthorityResourceAdapter {
    private final OperatorAuthorityResourceService operatorAuthorityResourceService;
    private final AccountQueryService accountQueryService;

    public Map<Long, Set<RequestTaskType>> getUserScopedRequestTaskTypesByAccountId(String userId, Long accountId){
        return findUserScopedRequestTaskTypesByAccounts(userId, Set.of(accountId));
    }

    public Map<Long, Set<RequestTaskType>> getUserScopedRequestTaskTypesByAccountType(PmrvUser user, AccountType accountType){
        Set<Long> accountIds = accountQueryService.getAccountIdsByAccountType(new ArrayList<>(user.getAccounts()), accountType);
        return findUserScopedRequestTaskTypesByAccounts(user.getUserId(), accountIds);
    }

    private Map<Long, Set<RequestTaskType>> findUserScopedRequestTaskTypesByAccounts(String userId, Set<Long> accounts){
        Map<Long, Set<String>> requestTaskTypes = operatorAuthorityResourceService
            .findUserScopedRequestTaskTypesByAccounts(userId, accounts);
        return requestTaskTypes.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream().map(RequestTaskType::valueOf).collect(Collectors.toSet())
                )
            );
    }
}
