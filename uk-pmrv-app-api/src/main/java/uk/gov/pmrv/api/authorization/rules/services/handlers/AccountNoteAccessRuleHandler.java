package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.AccountNoteAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;


@Service("accountNoteAccessHandler")
@RequiredArgsConstructor
public class AccountNoteAccessRuleHandler implements AuthorizationResourceRuleHandler {
    
    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final AccountNoteAuthorityInfoProvider accountNoteAuthorityInfoProvider;
    
    @Override
    public void evaluateRules(final Set<AuthorizationRuleScopePermission> authorizationRules, 
                              final PmrvUser user, 
                              final String resourceId) {

        final Long accountId = accountNoteAuthorityInfoProvider.getAccountIdById(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(accountId)
                    .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }

}
