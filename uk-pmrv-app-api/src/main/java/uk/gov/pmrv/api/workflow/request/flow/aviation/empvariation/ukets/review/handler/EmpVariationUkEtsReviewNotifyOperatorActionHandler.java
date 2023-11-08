package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.handler;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service.EmpVariationUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation.EmpVariationUkEtsReviewNotifyOperatorValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewNotifyOperatorActionHandler 
	implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsReviewService empVariationUkEtsReviewService;
    private final EmpVariationUkEtsReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        PmrvUser pmrvUser, NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        reviewNotifyOperatorValidatorService.validate(requestTask, payload, pmrvUser);

        DecisionNotification decisionNotification = payload.getDecisionNotification();
        empVariationUkEtsReviewService.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);

        // complete task
        EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
        EmpVariationDeterminationType determinationType = taskPayload.getDetermination().getType();
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, determinationType,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
