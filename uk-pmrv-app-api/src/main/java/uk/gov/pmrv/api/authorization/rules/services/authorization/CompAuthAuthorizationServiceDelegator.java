package uk.gov.pmrv.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;

import java.util.List;
import java.util.Optional;

/**
 * Service that delegates {@link CompetentAuthorityEnum} related authorization to {@link uk.gov.pmrv.api.common.domain.enumeration.RoleType} based services.
 * Currently applicable only for REGULATOR
 */
@Service
@RequiredArgsConstructor
public class CompAuthAuthorizationServiceDelegator {
    private final List<CompAuthAuthorizationService> compAuthAuthorizationServices;

    /**
     * checks that user has access to competentAuthority
     * @param user the user to authorize.
     * @param competentAuthority the {@link CompetentAuthorityEnum} to check permission on.
     * @return if the user is authorized on competentAuthority.
     */
    public boolean isAuthorized(PmrvUser user, CompetentAuthorityEnum competentAuthority) {
        return getUserService(user)
                .map(compAuthAuthorizationService -> compAuthAuthorizationService.isAuthorized(user, competentAuthority)).orElse(false);
    }

    /**
     * checks that user has the permissions to competentAuthority
     * @param user the user to authorize.
     * @param competentAuthority the {@link CompetentAuthorityEnum} to check permission on.
     * @param permission the {@link Permission} to check
     * @return if the user has the permissions on the competentAuthority
     */
    public boolean isAuthorized(PmrvUser user, CompetentAuthorityEnum competentAuthority, Permission permission) {
        return getUserService(user)
                .map(compAuthAuthorizationService -> compAuthAuthorizationService.isAuthorized(user, competentAuthority, permission)).orElse(false);
    }

    private Optional<CompAuthAuthorizationService> getUserService(PmrvUser user) {
        return compAuthAuthorizationServices.stream()
                .filter(compAuthAuthorizationService -> compAuthAuthorizationService.getRoleType().equals(user.getRoleType()))
                .findAny();
    }
}
