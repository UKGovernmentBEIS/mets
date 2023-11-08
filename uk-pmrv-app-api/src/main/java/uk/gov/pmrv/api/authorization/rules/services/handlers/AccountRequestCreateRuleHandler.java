package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Set;

@Service("accountRequestCreateHandler")
@RequiredArgsConstructor
public class AccountRequestCreateRuleHandler implements AuthorizationResourceRuleHandler {
    
    private final PmrvAuthorizationService pmrvAuthorizationService;
    
    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user,
            String resourceId) {
        if (authorizationRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (resourceId != null) {
        	authorizationRules.forEach(rule -> pmrvAuthorizationService.authorize(user,
                    AuthorizationCriteria.builder().accountId(Long.valueOf(resourceId)).permission(rule.getPermission()).build()));
        }
    }
}
