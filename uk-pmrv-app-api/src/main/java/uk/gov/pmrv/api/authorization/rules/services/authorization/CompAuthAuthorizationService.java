package uk.gov.pmrv.api.authorization.rules.services.authorization;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;

public interface CompAuthAuthorizationService {
    boolean isAuthorized(PmrvUser user, CompetentAuthorityEnum competentAuthority);
    boolean isAuthorized(PmrvUser user, CompetentAuthorityEnum competentAuthority, Permission permission);
    RoleType getRoleType();
}
