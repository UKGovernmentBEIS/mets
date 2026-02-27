package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Outcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2SubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2RequestPeerReviewValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BDRS2ApplicationPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final BDRS2SubmitService bdrs2SubmitService;
    private final BDRS2RequestPeerReviewValidator bdrs2RequestPeerReviewValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        bdrs2RequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);

        bdrs2SubmitService.requestPeerReview(requestTask, peerReviewer, appUser);
        requestService.addActionToRequest(request,
                null,
                RequestActionType.BDRS2_APPLICATION_PEER_REVIEW_REQUESTED,
                userId);
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.BDRS2_REGULATOR_REVIEW_OUTCOME, BDRS2Outcome.PEER_REVIEW_REQUIRED
        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDRS2_REQUEST_PEER_REVIEW);
    }
}
