package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

@RequiredArgsConstructor
@Service
public class ALRRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final ALRValidationService alrValidationService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final ALRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (ALRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.ALR_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        alrValidationService.validateALR(taskPayload.getAlr());
    }
}
