package uk.gov.pmrv.api.authorization.rules.services.handlers;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.EmpAuthorityInfoProvider;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.util.Set;

@Service("empAccessHandler")
@RequiredArgsConstructor
public class EmpAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final AppAuthorizationService appAuthorizationService;
    private final EmpAuthorityInfoProvider empAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link MetsErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanEntity}
     * with id the {@code resourceId} and permission the {@link uk.gov.netz.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, AppUser user, String resourceId) {
        Long accountId = empAuthorityInfoProvider.getEmpAccountById(resourceId);

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .requestResources(Map.of(ResourceType.ACCOUNT, accountId.toString()))
                .permission(rule.getPermission())
                .build();
            appAuthorizationService.authorize(user, authorizationCriteria);
        });
    }

}