package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service("caRequestCreateHandler")
@RequiredArgsConstructor
public class CaRequestCreateRuleHandler implements AuthorizationResourceRuleHandler {
    
    private final PmrvAuthorizationService pmrvAuthorizationService;
    
    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user,
            String resourceId) {
        if (authorizationRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

		authorizationRules.forEach(rule -> pmrvAuthorizationService.authorize(user, AuthorizationCriteria.builder()
				.competentAuthority(user.getCompetentAuthority())
				.permission(rule.getPermission())
				.build()));
    }
}
