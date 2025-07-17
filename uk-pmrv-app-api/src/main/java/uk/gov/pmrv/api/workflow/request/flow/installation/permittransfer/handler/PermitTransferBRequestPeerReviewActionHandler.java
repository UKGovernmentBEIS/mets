package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitTransferBRequestPeerReviewActionHandler
    implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitReviewRequestPeerReviewValidator permitReviewRequestPeerReviewValidator;
    private final PermitTransferBReviewService permitTransferBReviewService;
    private final RequestService requestService;
    private final WorkflowService workflowService;


    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PeerReviewRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitReviewRequestPeerReviewValidator.validate(
            requestTask,
            RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW,
            payload, 
            appUser
        );

        permitTransferBReviewService.saveRequestPeerReviewAction(requestTask, payload.getPeerReviewer(), appUser);

        requestService.addActionToRequest(requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED,
            appUser.getUserId()
        );

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED
            )
        );

    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW);
    }
}
