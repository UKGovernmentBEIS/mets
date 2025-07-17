package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirRequestPayload;

import java.util.Set;

@Service
public class AirApplicationReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final AirRequestPayload requestPayload = (AirRequestPayload) request.getPayload();

        return AirApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AIR_APPLICATION_REVIEW_PAYLOAD)
            .airImprovements(requestPayload.getAirImprovements())
            .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
            .airAttachments(requestPayload.getAirAttachments())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.AIR_APPLICATION_REVIEW);
    }
}
