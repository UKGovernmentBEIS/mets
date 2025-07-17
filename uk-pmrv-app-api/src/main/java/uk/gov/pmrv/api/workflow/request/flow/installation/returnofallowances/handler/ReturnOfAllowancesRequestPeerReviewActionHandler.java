package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReturnOfAllowancesRequestPeerReviewActionHandler implements
    RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ReturnOfAllowancesValidator returnOfAllowancesValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final ReturnOfAllowancesService returnOfAllowancesService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final ReturnOfAllowancesApplicationSubmitRequestTaskPayload payload =
            (ReturnOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final String peerReviewer = actionPayload.getPeerReviewer();

        returnOfAllowancesValidator.validate(payload.getReturnOfAllowances());
        peerReviewerTaskAssignmentValidator
            .validate(RequestTaskType.RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW, peerReviewer, appUser);

        final String regulatorReviewer = appUser.getUserId();
        returnOfAllowancesService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
            regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.RETURN_OF_ALLOWANCES_SUBMIT_OUTCOME, ReturnOfAllowancesSubmitOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW);
    }
}
