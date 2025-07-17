package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
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
                         final AppUser appUser) {
        // Validate Peer review
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.DOAL_APPLICATION_PEER_REVIEW,
                taskActionPayload.getPeerReviewer(), appUser);

        // Validate DOAL
        doalSubmitValidator.validate(taskPayload);
    }
}
