package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyService;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreRequestPeerReviewValidator;

@Component
@RequiredArgsConstructor
public class DreRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload>{
	
	private final RequestTaskService requestTaskService;
    private final DreApplyService dreApplyService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final DreRequestPeerReviewValidator dreRequestPeerReviewValidator;

	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
			PeerReviewRequestTaskActionPayload taskActionPayload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = pmrvUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        dreRequestPeerReviewValidator.validate(requestTask, taskActionPayload, pmrvUser);
        
        dreApplyService.requestPeerReview(requestTask, peerReviewer);
        requestService.addActionToRequest(request, null, RequestActionType.DRE_APPLICATION_PEER_REVIEW_REQUESTED, userId);
        
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.PEER_REVIEW_REQUIRED
            )
        );
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.DRE_REQUEST_PEER_REVIEW);
	}

}
