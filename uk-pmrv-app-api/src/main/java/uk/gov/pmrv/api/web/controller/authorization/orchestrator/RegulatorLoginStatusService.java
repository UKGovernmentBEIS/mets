package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.common.constants.RoleTypeConstants;

@Service
public class RegulatorLoginStatusService extends UserRoleLoginStatusAbstractService {

    public RegulatorLoginStatusService(AuthorityRepository authorityRepository) {
        super(authorityRepository);
    }

    @Override
    public String getRoleType() {
        return RoleTypeConstants.REGULATOR;
    }
}
