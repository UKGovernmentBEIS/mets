package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationRegulatorReviewSubmitRequestTaskPayload;

@RequiredArgsConstructor
@Service
public class BDRRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final BDRValidationService bdrValidationService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final BDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (BDRApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.BDR_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        bdrValidationService.validateBDR(taskPayload.getBdr());
    }

}
