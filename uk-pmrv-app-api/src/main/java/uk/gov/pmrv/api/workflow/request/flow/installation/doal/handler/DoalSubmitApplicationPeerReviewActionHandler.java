package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation.DoalRequestPeerReviewValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DoalSubmitApplicationPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final DoalRequestPeerReviewValidator doalRequestPeerReviewValidator;
    private final DoalSubmitService doalSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        PeerReviewRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final DoalApplicationSubmitRequestTaskPayload taskPayload =
                (DoalApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate
        doalRequestPeerReviewValidator.validate(taskPayload, taskActionPayload, pmrvUser);

        // Send to Peer Review
        doalSubmitService.requestPeerReview(requestTask, taskActionPayload.getPeerReviewer(), pmrvUser);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.DOAL_SUBMIT_OUTCOME, DoalSubmitOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.DOAL_REQUEST_PEER_REVIEW);
    }
}
