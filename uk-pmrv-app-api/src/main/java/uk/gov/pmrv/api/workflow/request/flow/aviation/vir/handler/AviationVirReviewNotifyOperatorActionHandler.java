package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.domain.RegulatorImprovementResponse;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.validation.VirReviewNotifyOperatorValidator;

@Component
@RequiredArgsConstructor
public class AviationVirReviewNotifyOperatorActionHandler 
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirReviewService virReviewService;
    private final VirReviewNotifyOperatorValidator virReviewNotifyOperatorValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final AviationVirApplicationReviewRequestTaskPayload taskPayload = 
            (AviationVirApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate review and action payload
        virReviewNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Submit review
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        virReviewService.submitReview(requestTask, decisionNotification, appUser);

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
        return List.of(RequestTaskActionType.AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
