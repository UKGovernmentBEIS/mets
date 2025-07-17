package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;


@Service
@RequiredArgsConstructor
public class AviationDoECorsiaPopulateRequestMetadataWithReportableEmissionsService {

    private final RequestService requestService;

    @Transactional
    public void updateRequestMetadata(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
        final AviationDoECorsiaRequestMetadata metadata = (AviationDoECorsiaRequestMetadata) request.getMetadata();

        metadata.setEmissions(requestPayload.getDoe().getEmissions().getEmissionsAllInternationalFlights());
        metadata.setTotalEmissionsOffsettingFlights(requestPayload.getDoe().getEmissions().getEmissionsFlightsWithOffsettingRequirements());
        metadata.setTotalEmissionsClaimedReductions(requestPayload.getDoe().getEmissions().getEmissionsClaimFromCorsiaEligibleFuels());
    }
}
