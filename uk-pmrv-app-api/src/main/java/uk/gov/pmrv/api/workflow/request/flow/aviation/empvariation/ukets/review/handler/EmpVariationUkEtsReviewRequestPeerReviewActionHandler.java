package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewRequestPeerReviewValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewRequestPeerReviewActionHandler 
	implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {
	
	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsReviewService empVariationUkEtsReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final EmpVariationUkEtsReviewRequestPeerReviewValidatorService requestPeerReviewValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, PeerReviewRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        String selectedPeerReviewer = payload.getPeerReviewer();

        requestPeerReviewValidatorService.validate(requestTask, selectedPeerReviewer, appUser);
        empVariationUkEtsReviewService.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, appUser);
        requestService.addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED,
            appUser.getUserId()
        );

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW);
    }
}
