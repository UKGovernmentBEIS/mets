package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;


@Service
@RequiredArgsConstructor
public class AviationDoECorsiaUpdateReportableEmissionsService {

    private final RequestService requestService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;

    @Transactional
    public void updateReportableEmissions(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationDoECorsiaRequestPayload requestPayload = (AviationDoECorsiaRequestPayload) request.getPayload();
        final AviationDoECorsiaRequestMetadata metadata = (AviationDoECorsiaRequestMetadata) request.getMetadata();
        final AviationReportableEmissionsSaveParams saveParams = AviationReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(metadata.getYear())
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(requestPayload.getDoe().getEmissions().getEmissionsAllInternationalFlights())
                        .reportableOffsetEmissions(requestPayload.getDoe().getEmissions().getEmissionsFlightsWithOffsettingRequirements())
                        .reportableReductionClaimEmissions(requestPayload.getDoe().getEmissions().getEmissionsClaimFromCorsiaEligibleFuels())
                        .build()
                )
                .isExempted(metadata.isExempted())
                .isFromDre(true)
                .build();

        aviationReportableEmissionsService.saveReportableEmissions(saveParams, EmissionTradingScheme.CORSIA);
    }
}
