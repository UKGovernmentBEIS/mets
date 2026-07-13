package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.rules.services.authorityinfo.providers.VerificationBodyAuthorityInfoProvider;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationBodyAuthorityService implements VerificationBodyAuthorityInfoProvider {

    private final VerificationBodyQueryService verificationBodyQueryService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> getThirdPartyDataProviderId(Long verificationBodyId) {
        return Optional.ofNullable(
            verificationBodyQueryService.getVerificationBodyById(verificationBodyId).getThirdPartyDataProviderId());
    }
}
