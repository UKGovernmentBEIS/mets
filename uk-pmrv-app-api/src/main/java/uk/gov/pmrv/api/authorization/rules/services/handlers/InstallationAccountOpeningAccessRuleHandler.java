package uk.gov.pmrv.api.authorization.rules.services.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Map;
import java.util.Set;

@Service("installationAccountOpeningRuleHandler")
@RequiredArgsConstructor
public class InstallationAccountOpeningAccessRuleHandler implements AuthorizationResourceRuleHandler {

    @Override
    public void evaluateRules(Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        if (resourceId != null || authorizationRules.isEmpty() || !RoleTypeConstants.OPERATOR.equals(user.getRoleType())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
