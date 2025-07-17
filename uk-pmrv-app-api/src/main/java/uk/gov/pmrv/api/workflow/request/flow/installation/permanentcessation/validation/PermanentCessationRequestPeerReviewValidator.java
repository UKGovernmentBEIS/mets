package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@RequiredArgsConstructor
@Service
public class PermanentCessationRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validate(final PeerReviewRequestTaskActionPayload taskActionPayload, final AppUser appUser) {

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);
    }
}
