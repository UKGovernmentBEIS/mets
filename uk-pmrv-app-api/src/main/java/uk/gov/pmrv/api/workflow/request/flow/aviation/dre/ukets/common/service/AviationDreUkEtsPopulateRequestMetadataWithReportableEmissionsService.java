package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;

@Service
@RequiredArgsConstructor
public class AviationDreUkEtsPopulateRequestMetadataWithReportableEmissionsService {

    private final RequestService requestService;

    @Transactional
    public void updateRequestMetadata(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AviationDreUkEtsRequestPayload requestPayload = (AviationDreUkEtsRequestPayload) request.getPayload();
        final AviationDreRequestMetadata metadata = (AviationDreRequestMetadata) request.getMetadata();
        metadata.setEmissions(requestPayload.getDre().getTotalReportableEmissions());
    }
}
