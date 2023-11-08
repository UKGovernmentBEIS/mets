package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private final AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        RequestAviationAccountInfo aviationAccountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        AviationAerCorsiaRequestPayload aerCorsiaRequestPayload = (AviationAerCorsiaRequestPayload) request.getPayload();
        AviationAerRequestMetadata aerRequestMetadata = (AviationAerRequestMetadata) request.getMetadata();

        return aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload(
                aerCorsiaRequestPayload,
                aviationAccountInfo,
                RequestTaskPayloadType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD,
                aerRequestMetadata.getYear()
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT);
    }
}
