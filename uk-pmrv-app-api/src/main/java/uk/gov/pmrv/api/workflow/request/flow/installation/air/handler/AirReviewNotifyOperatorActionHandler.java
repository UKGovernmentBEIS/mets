package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import java.util.List;
import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.validation.AirReviewNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.RegulatorAirImprovementResponse;

@Component
@RequiredArgsConstructor
public class AirReviewNotifyOperatorActionHandler implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AirReviewService reviewService;
    private final AirReviewNotifyOperatorValidator reviewNotifyOperatorValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final AirApplicationReviewRequestTaskPayload taskPayload = (AirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate review and action payload
        reviewNotifyOperatorValidator.validate(requestTask, payload, pmrvUser);

        // Submit review
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        reviewService.submitReview(requestTask, decisionNotification, pmrvUser);

        // Complete task
        final boolean airNeedsImprovements = taskPayload.getRegulatorReviewResponse().getRegulatorImprovementResponses()
                .values().stream().anyMatch(RegulatorAirImprovementResponse::getImprovementRequired);

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.COMPLETED,
                        BpmnProcessConstants.AIR_NEEDS_IMPROVEMENTS, airNeedsImprovements)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AIR_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
