package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderReviewRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final PermitSurrenderReviewDeterminationHandlerService permitSurrenderReviewDeterminationHandlerService;

    public void validate(RequestTask requestTask, PeerReviewRequestTaskActionPayload payload, AppUser appUser) {
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW, payload.getPeerReviewer(), appUser);

        PermitSurrenderApplicationReviewRequestTaskPayload requestTaskPayload =
            (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask.getPayload();

        permitSurrenderReviewDeterminationHandlerService.validateReview(requestTaskPayload.getReviewDecision(), requestTaskPayload.getReviewDetermination());
    }
}
