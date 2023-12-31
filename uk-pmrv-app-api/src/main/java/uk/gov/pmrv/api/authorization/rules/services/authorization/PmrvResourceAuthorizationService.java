package uk.gov.pmrv.api.authorization.rules.services.authorization;

import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;

public interface PmrvResourceAuthorizationService {
    boolean isAuthorized(PmrvUser user, AuthorizationCriteria criteria);
    ResourceType getResourceType();
}
