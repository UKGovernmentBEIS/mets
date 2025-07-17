package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final List<UserRoleLoginStatusService> userLoginStatusServices;

    public UserDomainsLoginStatusInfo getUserDomainsLoginStatusInfo(AppUser user) {
        return getUserLoginStatusService(user.getRoleType())
            .map(service -> service.getUserDomainsLoginStatusInfo(user.getUserId()))
            .orElseGet(() -> buildNoRoleTypeInfo());
    }

    private Optional<UserRoleLoginStatusService> getUserLoginStatusService(String roleType) {
        return userLoginStatusServices.stream()
            .filter(service -> service.getRoleType().equals(roleType))
            .findFirst();
    }

    private UserDomainsLoginStatusInfo buildNoRoleTypeInfo() {
        EnumMap<AccountType, LoginStatus> loginStatuses = new EnumMap<>(AccountType.class);
        loginStatuses.put(AccountType.INSTALLATION, LoginStatus.NO_ROLE_TYPE);
        loginStatuses.put(AccountType.AVIATION, LoginStatus.NO_ROLE_TYPE);

        return UserDomainsLoginStatusInfo.builder()
            .domainsLoginStatuses(loginStatuses)
            .build();
    }
}
