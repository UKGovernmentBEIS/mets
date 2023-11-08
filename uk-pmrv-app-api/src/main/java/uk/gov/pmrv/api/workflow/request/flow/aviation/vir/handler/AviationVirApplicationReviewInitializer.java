package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirRequestPayload;

@Service
public class AviationVirApplicationReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {
        
        final AviationVirRequestPayload requestPayload = (AviationVirRequestPayload) request.getPayload();

        return AviationVirApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.AVIATION_VIR_APPLICATION_REVIEW_PAYLOAD)
                .verificationData(requestPayload.getVerificationData())
                .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
                .virAttachments(requestPayload.getVirAttachments())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AVIATION_VIR_APPLICATION_REVIEW);
    }
}
