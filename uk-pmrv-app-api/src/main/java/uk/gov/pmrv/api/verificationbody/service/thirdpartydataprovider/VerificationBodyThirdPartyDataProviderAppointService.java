package uk.gov.pmrv.api.verificationbody.service.thirdpartydataprovider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.thirdpartydataprovider.service.ThirdPartyDataProviderQueryService;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

@RequiredArgsConstructor
@Service
public class VerificationBodyThirdPartyDataProviderAppointService {
    
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final ThirdPartyDataProviderQueryService thirdPartyDataProviderQueryService;

    @Transactional
    public void appointThirdPartyDataProviderToVerificationBody(Long thirdPartyDataProviderId, Long verificationBodyId) {
        // Validate that verification body exists and is in active state
        VerificationBody verificationBody = verificationBodyQueryService.getVerificationBodyById(verificationBodyId);;

        if (!VerificationBodyStatus.ACTIVE.equals(verificationBody.getStatus())) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_INVALID_STATUS);
        }
        
        if (!thirdPartyDataProviderQueryService.existsById(thirdPartyDataProviderId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, thirdPartyDataProviderId);
        }

        if (thirdPartyDataProviderId.equals(verificationBody.getThirdPartyDataProviderId())) {
            throw new BusinessException(ErrorCode.THIRD_PARTY_DATA_PROVIDER_ALREADY_APPOINTED_TO_VERIFICATION_BODY);
        }
        
        //appoint
        verificationBody.setThirdPartyDataProviderId(thirdPartyDataProviderId);
    }
}
