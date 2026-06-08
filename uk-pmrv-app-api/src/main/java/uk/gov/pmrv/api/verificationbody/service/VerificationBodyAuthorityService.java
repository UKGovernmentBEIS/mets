package uk.gov.pmrv.api.verificationbody.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.VerificationBodyAuthorityInfoProvider;

@Service
public class VerificationBodyAuthorityService implements VerificationBodyAuthorityInfoProvider {

    /**
     * Dummy implementation required by the authorization library.
     * Third-party data provider is not applicable in this context.
     */
    @Override
    public Optional<Long> getThirdPartyDataProviderId(Long verificationBodyId) {
        return Optional.empty();
    }
}
