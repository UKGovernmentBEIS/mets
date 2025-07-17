package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationDoECorsiaRequestPeerReviewActionHandler
        implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationDoECorsiaSubmitService submitService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW, peerReviewer, appUser);

        submitService.requestPeerReview(requestTask, peerReviewer);
        requestService.addActionToRequest(request, null, RequestActionType.AVIATION_DOE_CORSIA_PEER_REVIEW_REQUESTED, userId);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.AVIATION_DOE_CORSIA_SUBMIT_OUTCOME, AviationDoECorsiaSubmitOutcome.PEER_REVIEW_REQUIRED
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_DOE_CORSIA_REQUEST_PEER_REVIEW);
    }
}
