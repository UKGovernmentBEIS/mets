package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;

@RequiredArgsConstructor
@Service
public class NERRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final NERValidationService nerValidationService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                (NERApplicationRegulatorReviewSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(RequestTaskType.NER_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        nerValidationService.validateNer(taskPayload.getNer());
    }
}
