package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.user.core.service.UserLoginDomainService;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserStateDTO;

@Service
@RequiredArgsConstructor
public class UserAuthorityQueryOrchestrator {

    private final UserStatusService userStatusService;
    private final UserLoginDomainService userLoginDomainService;

    public UserStateDTO getUserState(PmrvUser user) {
        String userId = user.getUserId();
        return UserStateDTO.builder()
            .userId(userId)
            .roleType(user.getRoleType())
            .lastLoginDomain(userLoginDomainService.getUserLastLoginDomain(userId))
            .domainsLoginStatuses(userStatusService.getUserDomainsLoginStatusInfo(user))
            .build();
    }
}
