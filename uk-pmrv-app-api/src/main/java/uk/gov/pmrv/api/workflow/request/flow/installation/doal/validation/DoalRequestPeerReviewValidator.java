package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class DoalRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final DoalSubmitValidator doalSubmitValidator;

    public void validate(final DoalApplicationSubmitRequestTaskPayload taskPayload, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final PmrvUser pmrvUser) {
        // Validate Peer review
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.DOAL_APPLICATION_PEER_REVIEW,
                taskActionPayload.getPeerReviewer(), pmrvUser);

        // Validate DOAL
        doalSubmitValidator.validate(taskPayload);
    }
}
