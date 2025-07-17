package uk.gov.pmrv.api.authorization.rules.services.handlers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.PermitAuthorityInfoProvider;

@ExtendWith(MockitoExtension.class)
class EmpAccessRuleHandlerTest {

    private final AppUser REGULATOR_USER = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
    private final AppUser OPERATOR_USER = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
    private final AppUser VERIFIER_USER = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private PermitAuthorityInfoProvider permitAuthorityInfoProvider;

    @InjectMocks
    private PermitAccessRuleHandler permitAccessRuleHandler;

    @Test
    void single_rule() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 =
            AuthorizationRuleScopePermission.builder()
                .resourceType("EMP")
                .handler("empAccessHandler")
                .build();

        when(permitAuthorityInfoProvider.getPermitAccountById("UK-E-AV-00001")).thenReturn(1l);

        AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            .requestResources(Map.of(ResourceType.ACCOUNT, "1"))
            .build();

        permitAccessRuleHandler.evaluateRules(Set.of(authorizationRulePermissionScope1), REGULATOR_USER, "UK-E-AV-00001");
        verify(appAuthorizationService, times(1)).authorize(REGULATOR_USER, authorizationCriteria);

        permitAccessRuleHandler.evaluateRules(Set.of(authorizationRulePermissionScope1), OPERATOR_USER, "UK-E-AV-00001");
        verify(appAuthorizationService, times(1)).authorize(OPERATOR_USER, authorizationCriteria);

        permitAccessRuleHandler.evaluateRules(Set.of(authorizationRulePermissionScope1), VERIFIER_USER, "UK-E-AV-00001");
        verify(appAuthorizationService, times(1)).authorize(VERIFIER_USER, authorizationCriteria);
    }

}