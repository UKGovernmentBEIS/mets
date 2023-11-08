package uk.gov.pmrv.api.authorization.rules.services.authorization;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.AccountAuthorityInfoProvider;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

/**
 * Service that checks if a VERIFIER user is authorized on an account.
 */
@Service
@RequiredArgsConstructor
public class VerifierAccountAuthorizationService implements AccountAuthorizationService {

    private final AccountAuthorityInfoProvider accountAuthorityInfoProvider;
    private final VerifierVerificationBodyAuthorizationService verifierVerificationBodyAuthorizationService;
    private final VerifierAccountAccessService verifierAccountAccessService;

    /**
     * Checks that VERIFIER has access to account.
     * @param user the user to authorize
     * @param accountId the account to check permission on
     * @return if the VERIFIER is authorized on account.
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId) {
        
        final boolean authorized = this.checkAuthorizedAccount(user, accountId);
        if (!authorized) {
            return false;
        }

        Optional<Long> accountVerificationBodyOptional = accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId);
        return accountVerificationBodyOptional
            .map(accountVerificationBody -> verifierVerificationBodyAuthorizationService.isAuthorized(user, accountVerificationBody))
            .orElse(false);
    }

    /**
     * Checks that VERIFIER has the permissions to account.
     * @param user the user to authorize
     * @param accountId the account to check permission on
     * @param permission the {@link Permission} to check
     * @return if the VERIFIER has the permissions on the account
     */
    @Override
    public boolean isAuthorized(PmrvUser user, Long accountId, Permission permission) {
        
        final boolean authorized = this.checkAuthorizedAccount(user, accountId);
        if (!authorized) {
            return false;
        }
        
        Optional<Long> accountVerificationBodyOptional = accountAuthorityInfoProvider.getAccountVerificationBodyId(accountId);
        return accountVerificationBodyOptional
            .map(accountVerificationBody -> verifierVerificationBodyAuthorizationService.isAuthorized(user, accountVerificationBody, permission))
            .orElse(false);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }

    private boolean checkAuthorizedAccount(final PmrvUser user, final Long accountId) {

        return verifierAccountAccessService.findAuthorizedAccountIds(user).contains(accountId);
    }
}
