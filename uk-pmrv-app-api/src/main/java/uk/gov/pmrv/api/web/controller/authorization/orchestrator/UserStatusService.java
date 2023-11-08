package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final List<UserRoleLoginStatusService> userLoginStatusServices;

    public UserDomainsLoginStatusInfo getUserDomainsLoginStatusInfo(PmrvUser user) {
        return getUserLoginStatusService(user.getRoleType())
            .map(service -> service.getUserDomainsLoginStatusInfo(user.getUserId()))
            .orElse(buildNoAuthorityUserLoginStatuses());
    }

    private Optional<UserRoleLoginStatusService> getUserLoginStatusService(RoleType roleType) {
        return userLoginStatusServices.stream()
            .filter(service -> service.getRoleType().equals(roleType))
            .findFirst();
    }

    private UserDomainsLoginStatusInfo buildNoAuthorityUserLoginStatuses() {
        EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
        loginStatuses.put(AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY);
        loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_AUTHORITY);

        return UserDomainsLoginStatusInfo.builder()
            .domainsLoginStatuses(loginStatuses)
            .build();
    }
}
