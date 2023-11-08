package uk.gov.pmrv.api.authorization.rules.services.handlers;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestNoteAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;


@Service("requestNoteAccessHandler")
@RequiredArgsConstructor
public class RequestNoteAccessRuleHandler implements AuthorizationResourceRuleHandler {

    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final RequestNoteAuthorityInfoProvider requestNoteAuthorityInfoProvider;

    @Override
    public void evaluateRules(final Set<AuthorizationRuleScopePermission> authorizationRules,
                              final PmrvUser user,
                              final String resourceId) {

        final RequestAuthorityInfoDTO requestInfo =
            requestNoteAuthorityInfoProvider.getRequestNoteInfo(Long.parseLong(resourceId));

        authorizationRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                .accountId(requestInfo.getAuthorityInfo().getAccountId())
                .competentAuthority(requestInfo.getAuthorityInfo().getCompetentAuthority())
                .verificationBodyId(requestInfo.getAuthorityInfo().getVerificationBodyId())
                .build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
