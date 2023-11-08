package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

import java.util.Set;

@Service
public class VirApplicationReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final VirRequestPayload requestPayload = (VirRequestPayload) request.getPayload();

        return VirApplicationReviewRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.VIR_APPLICATION_REVIEW_PAYLOAD)
                .verificationData(requestPayload.getVerificationData())
                .operatorImprovementResponses(requestPayload.getOperatorImprovementResponses())
                .virAttachments(requestPayload.getVirAttachments())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.VIR_APPLICATION_REVIEW);
    }
}
