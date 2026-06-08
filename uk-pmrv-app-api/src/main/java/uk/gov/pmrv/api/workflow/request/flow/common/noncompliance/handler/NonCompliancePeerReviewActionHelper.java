package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonCompliancePeerReviewRequestedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Component
@RequiredArgsConstructor
public class NonCompliancePeerReviewActionHelper {

    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final RequestService requestService;

    public NonCompliancePeerReviewRequestedRequestActionPayload buildPeerReviewRequestedPayload(String peerReviewerUserId) {
        return NonCompliancePeerReviewRequestedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_PEER_REVIEW_REQUESTED_PAYLOAD)
                .submittedTo(requestActionUserInfoResolver.getUserFullName(peerReviewerUserId))
                .build();
    }

    public String resolveMainRegulatorName(String requestId, RequestActionType peerReviewRequestedActionType) {
        return requestService.findSubmitterByRequestIdAndActionType(requestId, peerReviewRequestedActionType)
                .orElse(null);
    }
}
