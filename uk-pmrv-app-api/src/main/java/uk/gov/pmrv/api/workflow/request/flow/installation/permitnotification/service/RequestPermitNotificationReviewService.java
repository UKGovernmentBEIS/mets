package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class RequestPermitNotificationReviewService {

    @Transactional
    public void saveReviewDecision(PermitNotificationSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload,
                                   RequestTask requestTask) {
        final PermitNotificationReviewDecision reviewDecision = taskActionPayload.getReviewDecision();

        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        taskPayload.setReviewDecision(reviewDecision);
    }

    @Transactional
    public void saveRequestPeerReviewAction(RequestTask requestTask, String selectedPeerReview, PmrvUser pmrvUser) {
        final Request request = requestTask.getRequest();
        final PermitNotificationApplicationReviewRequestTaskPayload taskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();

        requestPayload.setReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setRegulatorReviewer(pmrvUser.getUserId());
        requestPayload.setRegulatorPeerReviewer(selectedPeerReview);
    }
}
