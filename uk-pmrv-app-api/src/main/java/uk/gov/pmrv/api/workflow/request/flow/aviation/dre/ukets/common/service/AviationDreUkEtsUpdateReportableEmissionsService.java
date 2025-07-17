package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class AviationDreUkEtsUpdateReportableEmissionsService {

    private final RequestService requestService;
    private final AviationReportableEmissionsService aviationReportableEmissionsService;

    @Transactional
    public void updateReportableEmissions(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
        final AviationDreRequestMetadata metadata = (AviationDreRequestMetadata) request.getMetadata();
        final AviationReportableEmissionsSaveParams saveParams = AviationReportableEmissionsSaveParams.builder()
                .accountId(request.getAccountId())
                .year(metadata.getYear())
                .reportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                        .reportableEmissions(requestPayload.getDre().getTotalReportableEmissions())
                        .build()
                )
                .isExempted(metadata.isExempted())
                .isFromDre(true)
                .build();

        aviationReportableEmissionsService.saveReportableEmissions(saveParams, EmissionTradingScheme.UK_ETS_AVIATION);
    }
}
