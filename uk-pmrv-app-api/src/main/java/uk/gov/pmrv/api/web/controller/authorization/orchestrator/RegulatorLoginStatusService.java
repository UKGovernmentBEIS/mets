package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
public class RegulatorLoginStatusService extends UserRoleLoginStatusAbstractService {

    public RegulatorLoginStatusService(AuthorityRepository authorityRepository, UserAuthService userAuthService) {
        super(authorityRepository, userAuthService);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }
}
