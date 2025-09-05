package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIRequestPeerReviewValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HSETIApplicationPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final HSETIRegulatorReviewSubmitService hsetiRegulatorReviewSubmitService;
    private final HSETIRequestPeerReviewValidator hsetiRequestPeerReviewValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        hsetiRequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);

        hsetiRegulatorReviewSubmitService.requestPeerReview(requestTask, peerReviewer, appUser);
        requestService.addActionToRequest(request,
                null,
                RequestActionType.HSE_TI_APPLICATION_PEER_REVIEW_REQUESTED,
                userId);
        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.HSE_TI_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED
        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.HSE_TI_REQUEST_PEER_REVIEW);
    }
}