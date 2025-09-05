package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAuthorityReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRAuthorityResponseSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.util.Set;

@Service
public class ALRAuthorityResponseInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        return ALRAuthorityResponseSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD)
                .regulatorPreliminaryAllocations(requestPayload.getRegulatorReviewOutcome().getAllocations())
                .alrAttachments(requestPayload.getAlrAttachments())
                .authorityReviewOutcome(ALRApplicationAuthorityReviewOutcome.builder().alr(requestPayload.getAlr()).build())
                .alrFileVersion(requestPayload.getAlrFileVersion())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.ALR_AUTHORITY_RESPONSE_SUBMIT);
    }
}
