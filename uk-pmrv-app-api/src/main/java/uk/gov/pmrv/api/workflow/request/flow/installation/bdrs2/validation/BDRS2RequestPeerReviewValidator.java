package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;

@RequiredArgsConstructor
@Service
public class BDRS2RequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final BDRS2ValidationService bdrs2ValidationService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.BDRS2_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        bdrs2ValidationService.validateBDRS2(taskPayload.getBdrs2());
    }
}
