package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class UserRoleLoginStatusAbstractService implements UserRoleLoginStatusService {

    private final AuthorityRepository authorityRepository;

    @Override
    public UserDomainsLoginStatusInfo getUserDomainsLoginStatusInfo(String userId) {
        LoginStatus loginStatus = getLoginStatus(userId);

        EnumMap<AccountType, LoginStatus> domainsLoginStatuses = new EnumMap<>(AccountType.class);
        domainsLoginStatuses.put(AccountType.INSTALLATION, loginStatus);
        domainsLoginStatuses.put(AccountType.AVIATION, loginStatus);

        return UserDomainsLoginStatusInfo.builder()
            .domainsLoginStatuses(domainsLoginStatuses)
            .build();
    }

    private LoginStatus getLoginStatus(String userId) {
        List<Authority> userAuthorities = authorityRepository.findByUserId(userId);

        if (!userAuthorities.isEmpty()) {
            List<Authority> activeUserAuthorities = getActiveUserAuthorities(userAuthorities);

            if(!activeUserAuthorities.isEmpty()) {
                List<Authority> activeUserAuthoritiesWithPermissions = getActiveUserAuthoritiesWithPermissions(activeUserAuthorities);

                //if user has only active authorities that do not have permissions assigned then return NO_AUTHORITY
                if(activeUserAuthoritiesWithPermissions.isEmpty()) {
                    return LoginStatus.NO_AUTHORITY;
                }

                return LoginStatus.ENABLED;

            } else {
                //if user has authorities, but none active
                if(userAuthorities.stream()
                    .anyMatch(ua -> ua.getStatus().equals(AuthorityStatus.ACCEPTED))){
                    return LoginStatus.ACCEPTED;
                }

                return userAuthorities.stream().anyMatch(ua -> ua.getStatus().equals(AuthorityStatus.TEMP_DISABLED))
                    ? LoginStatus.TEMP_DISABLED : LoginStatus.DISABLED;
            }
        } else {
        	return LoginStatus.NO_AUTHORITY;
        }
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
