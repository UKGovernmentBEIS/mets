package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator.WithholdingOfAllowancesValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WithholdingOfAllowancesRequestPeerReviewActionHandler implements
    RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WithholdingOfAllowancesValidator withholdingOfAllowancesValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final WithholdingOfAllowancesService withholdingOfAllowancesService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final WithholdingOfAllowancesApplicationSubmitRequestTaskPayload payload =
            (WithholdingOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        final String peerReviewer = actionPayload.getPeerReviewer();

        withholdingOfAllowancesValidator.validate(payload.getWithholdingOfAllowances());
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW, peerReviewer, pmrvUser);

        final String regulatorReviewer = pmrvUser.getUserId();
        withholdingOfAllowancesService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_REQUESTED,
            regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.WITHHOLDING_OF_ALLOWANCES_SUBMIT_OUTCOME, WithholdingOfAllowancesSubmitOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW);
    }
}
