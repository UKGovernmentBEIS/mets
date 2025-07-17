package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;

@Service
@RequiredArgsConstructor
public abstract class AviationAerUkEtsReviewInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final RequestVerificationService requestVerificationService;
    private final AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    protected abstract RequestTaskPayloadType getRequestTaskPayloadType();

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerUkEtsRequestPayload aerUkEtsRequestPayload = (AviationAerUkEtsRequestPayload) request.getPayload();
        AviationAerRequestMetadata aerUkEtsRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();
        
        // refresh Verification Body details
 		requestVerificationService.refreshVerificationReportVBDetails(aerUkEtsRequestPayload.getVerificationReport(),
 				request.getVerificationBodyId());

        return aviationAerUkEtsReviewMapper.toAviationAerUkEtsApplicationReviewRequestTaskPayload(
            aerUkEtsRequestPayload,
            aviationAccountInfo,
            getRequestTaskPayloadType(),
            aerUkEtsRequestMetadata.getYear()
        );
    }
}
