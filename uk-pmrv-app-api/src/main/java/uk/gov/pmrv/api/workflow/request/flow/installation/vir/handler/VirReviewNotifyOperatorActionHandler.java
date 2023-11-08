package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirReviewNotifyOperatorValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VirReviewNotifyOperatorActionHandler implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final VirReviewService virReviewService;
    private final VirReviewNotifyOperatorValidator virReviewNotifyOperatorValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final VirApplicationReviewRequestTaskPayload taskPayload = (VirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate review and action payload
        virReviewNotifyOperatorValidator.validate(requestTask, payload, pmrvUser);

        // Submit review
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        virReviewService.submitReview(requestTask, decisionNotification, pmrvUser);

        // Complete task
        boolean virNeedsImprovements = taskPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses()
                .values().stream().anyMatch(RegulatorImprovementResponse::isImprovementRequired);

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.COMPLETED,
                        BpmnProcessConstants.VIR_NEEDS_IMPROVEMENTS, virNeedsImprovements)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.VIR_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
