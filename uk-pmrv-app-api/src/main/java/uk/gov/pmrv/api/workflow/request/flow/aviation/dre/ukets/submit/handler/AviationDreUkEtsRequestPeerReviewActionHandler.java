package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service.RequestAviationDreUkEtsApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

@Component
@RequiredArgsConstructor
public class AviationDreUkEtsRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationDreUkEtsApplyService aviationDreApplyService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = pmrvUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW, peerReviewer, pmrvUser);

        aviationDreApplyService.requestPeerReview(requestTask, peerReviewer);
        requestService.addActionToRequest(request, null, RequestActionType.AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED, userId);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.DRE_SUBMIT_OUTCOME, AviationDreSubmitOutcome.PEER_REVIEW_REQUIRED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW);
    }
}