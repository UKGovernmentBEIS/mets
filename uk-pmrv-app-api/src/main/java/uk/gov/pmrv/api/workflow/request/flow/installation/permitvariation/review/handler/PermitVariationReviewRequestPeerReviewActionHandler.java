package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation.PermitVariationReviewRequestPeerReviewValidator;

import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_VARIATION_PEER_REVIEW_REQUESTED;

@Component
@RequiredArgsConstructor
public class PermitVariationReviewRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitVariationReviewService permitVariationReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final PermitVariationReviewRequestPeerReviewValidator permitVariationReviewRequestPeerReviewValidator;
    

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final PeerReviewRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        permitVariationReviewRequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);
        permitVariationReviewService.saveRequestPeerReviewAction(requestTask, peerReviewer, userId);
        requestService.addActionToRequest(request, null, PERMIT_VARIATION_PEER_REVIEW_REQUESTED, userId);
        
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW);
    }
}
