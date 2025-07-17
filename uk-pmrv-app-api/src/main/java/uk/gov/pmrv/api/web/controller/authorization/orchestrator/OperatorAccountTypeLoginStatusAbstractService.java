package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus.ACCEPTED;
import static uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus.DISABLED;
import static uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus.ENABLED;
import static uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus.NO_AUTHORITY;

@RequiredArgsConstructor
public abstract class OperatorAccountTypeLoginStatusAbstractService implements OperatorAccountTypeLoginStatusService {

    private final AccountQueryService accountQueryService;

    public abstract AccountType getAccountType();

    @Override
    public LoginStatus getLoginStatus(List<Authority> userAuthorities) {
        List<Authority> userAuthoritiesByAccountType = getUserAuthoritiesByAccountType(userAuthorities);

        if(!userAuthoritiesByAccountType.isEmpty()) {
            List<Authority> activeUserAuthoritiesByAccountType =
                getActiveUserAuthorities(userAuthoritiesByAccountType);

            if(!activeUserAuthoritiesByAccountType.isEmpty()) {
                List<Authority> activeUserAuthoritiesWithPermissionsByAccountType =
                    getActiveUserAuthoritiesWithPermissions(activeUserAuthoritiesByAccountType);
                return activeUserAuthoritiesWithPermissionsByAccountType.isEmpty() ? NO_AUTHORITY : ENABLED;
            } else {
                //if user has authorities, but none active
                return userAuthorities.stream()
                    .anyMatch(ua -> ua.getStatus().equals(AuthorityStatus.ACCEPTED))
                    ? ACCEPTED
                    : DISABLED;
            }
        }

        return NO_AUTHORITY;
    }

    private List<Authority> getUserAuthoritiesByAccountType(List<Authority> userAuthorities) {
        List<Long> accountIds = userAuthorities.stream().map(Authority::getAccountId).toList();
        Set<Long> accountIdsByDomain = accountQueryService.getAccountIdsByAccountType(accountIds, getAccountType());
        return userAuthorities.stream()
            .filter(au -> accountIdsByDomain.contains(au.getAccountId()))
            .collect(Collectors.toList());
    }

    private List<Authority> getActiveUserAuthorities(List<Authority> userAuthorities) {
        return userAuthorities.stream()
            .filter(au -> au.getStatus().equals(AuthorityStatus.ACTIVE)).collect(Collectors.toList());
    }

    private List<Authority> getActiveUserAuthoritiesWithPermissions(List<Authority> activeUserAuthorities) {
        return activeUserAuthorities.stream()
            .filter(au -> !au.getAuthorityPermissions().isEmpty())
            .collect(Collectors.toList());
    }
}
