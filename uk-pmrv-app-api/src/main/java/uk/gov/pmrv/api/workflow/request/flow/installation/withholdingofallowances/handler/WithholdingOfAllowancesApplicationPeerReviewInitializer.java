package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Set;

@Service
public class WithholdingOfAllowancesApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final WithholdingOfAllowancesRequestPayload requestPayload = (WithholdingOfAllowancesRequestPayload) request.getPayload();
        return WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD)
            .withholdingOfAllowances(requestPayload.getWithholdingOfAllowances())
            .sectionsCompleted(requestPayload.getWithholdingOfAllowancesSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW);
    }
}
