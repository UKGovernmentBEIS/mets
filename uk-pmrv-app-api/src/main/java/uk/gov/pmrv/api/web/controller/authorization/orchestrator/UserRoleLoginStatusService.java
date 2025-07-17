package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

public interface UserRoleLoginStatusService {

    UserDomainsLoginStatusInfo getUserDomainsLoginStatusInfo(String userId);

    String getRoleType();
}
