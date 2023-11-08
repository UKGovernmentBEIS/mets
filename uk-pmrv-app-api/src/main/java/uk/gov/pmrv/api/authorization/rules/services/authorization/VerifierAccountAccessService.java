package uk.gov.pmrv.api.authorization.rules.services.authorization;

import java.util.Set;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;

public interface VerifierAccountAccessService {

    Set<Long> findAuthorizedAccountIds(PmrvUser user);
}
