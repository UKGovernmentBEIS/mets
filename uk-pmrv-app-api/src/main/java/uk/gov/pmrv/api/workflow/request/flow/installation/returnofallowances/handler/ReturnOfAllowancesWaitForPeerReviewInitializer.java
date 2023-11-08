package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Set;

@Service
public class ReturnOfAllowancesWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final ReturnOfAllowancesRequestPayload requestPayload = (ReturnOfAllowancesRequestPayload) request.getPayload();
        return ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD)
            .returnOfAllowances(requestPayload.getReturnOfAllowances())
            .sectionsCompleted(requestPayload.getReturnOfAllowancesSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW);
    }
}
