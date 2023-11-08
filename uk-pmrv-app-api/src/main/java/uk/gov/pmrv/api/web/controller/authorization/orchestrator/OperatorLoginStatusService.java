package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus.NO_AUTHORITY;

@Service
@RequiredArgsConstructor
public class OperatorLoginStatusService implements UserRoleLoginStatusService {

    private final AuthorityRepository authorityRepository;
    private final List<OperatorAccountTypeLoginStatusService> operatorAccountTypeLoginStatusServices;

    @Override
    public UserDomainsLoginStatusInfo getUserDomainsLoginStatusInfo(String userId) {
        List<Authority> userAuthorities = authorityRepository.findByUserId(userId);

        EnumMap<AccountType, LoginStatus> domainsLoginStatuses = new EnumMap<>(AccountType.class);
        if(!userAuthorities.isEmpty()) {
            EnumSet.allOf(AccountType.class).forEach(
                accountType -> domainsLoginStatuses.put(accountType, getOperatorUserLoginStatus(userAuthorities, accountType))
            );
        } else {
            EnumSet.allOf(AccountType.class).forEach(
                accountType -> domainsLoginStatuses.put(accountType, NO_AUTHORITY)
            );
        }

        return UserDomainsLoginStatusInfo.builder()
            .domainsLoginStatuses(domainsLoginStatuses)
            .build();
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }

    private LoginStatus getOperatorUserLoginStatus(List<Authority> userAuthorities, AccountType accountType) {
        return getOperatorUserLoginStatusService(accountType)
            .map(service -> service.getLoginStatus(userAuthorities))
            .orElse(NO_AUTHORITY);
    }

    private Optional<OperatorAccountTypeLoginStatusService> getOperatorUserLoginStatusService(AccountType accountType) {
        return operatorAccountTypeLoginStatusServices.stream()
            .filter(service -> service.getAccountType().equals(accountType))
            .findFirst();
    }
}
