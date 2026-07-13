package uk.gov.pmrv.api.verificationbody.service.thirdpartydataprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.thirdpartydataprovider.domain.ThirdPartyDataProviderNameInfoDTO;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderQueryService;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VerificationBodyThirdPartyDataProviderService {

    private final VerificationBodyQueryService verificationBodyQueryService;
    private final ThirdPartyDataProviderService thirdPartyDataProviderService;
    private final ThirdPartyDataProviderQueryService thirdPartyDataProviderQueryService;

    public Optional<ThirdPartyDataProviderNameInfoDTO> getThirdPartyDataProviderNameInfoByVerificationBody(Long verificationBodyId) {
        return Optional.ofNullable(verificationBodyQueryService.getVerificationBodyById(verificationBodyId).getThirdPartyDataProviderId())
            .map(thirdPartyDataProviderService::getThirdPartyDataProviderNameInfoById);
    }

    public List<ThirdPartyDataProviderNameInfoDTO> getAllThirdPartyDataProviders() {
        return thirdPartyDataProviderQueryService.getAllThirdPartyDataProviders();
    }
}
