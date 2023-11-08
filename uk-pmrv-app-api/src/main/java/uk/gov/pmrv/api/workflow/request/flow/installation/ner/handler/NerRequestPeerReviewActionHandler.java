package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@Component
@RequiredArgsConstructor
public class NerRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerReviewValidator reviewValidator;
    private final RequestService requestService;
    private final NerApplyReviewService applyReviewService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PeerReviewRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final BigDecimal paymentAmount = requestTask.getRequest().getPayload().getPaymentAmount();
        
        reviewValidator.validateReviewTaskPayload(taskPayload, paymentAmount);
        reviewValidator.validatePeerReview(
            payload,
            pmrvUser
        );

        applyReviewService.saveRequestPeerReviewAction(requestTask, payload.getPeerReviewer(), pmrvUser);

        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.NER_PEER_REVIEW_REQUESTED,
            pmrvUser.getUserId()
        );

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, NerSubmitOutcome.PEER_REVIEW_REQUIRED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_REQUEST_PEER_REVIEW);
    }
}
