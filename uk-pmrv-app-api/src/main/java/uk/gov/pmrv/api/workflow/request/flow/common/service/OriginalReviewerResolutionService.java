package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class OriginalReviewerResolutionService {

    private final RequestTaskService requestTaskService;

    /**
     * Overrides the signatory in DecisionNotification with the original reviewer
     * if the task is a peer review.
     */
    @Transactional(readOnly = true)
    public void resolveAndOverrideSignatoryForPeerReview(Long taskId, DecisionNotification decisionNotification) {
        if(decisionNotification==null) {
            return;
        }

        RequestTask task = requestTaskService.findTaskById(taskId);

        if (isPeerReview(task)) {
            String originalReviewer = extractOriginalReviewerFromRequest(task);
            decisionNotification.setSignatory(originalReviewer);
        }
    }

    private boolean isPeerReview(RequestTask task) {
        return task.getType().isPeerReview();
    }

    private String extractOriginalReviewerFromRequest(RequestTask task) {
        Request request = task.getRequest();
        RequestPayload requestPayload = request.getPayload();
        return requestPayload.getRegulatorReviewer();
    }
}