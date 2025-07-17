package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation.EmpIssuanceCorsiaReviewNotifyOperatorValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
public class EmpIssuanceCorsiaReviewNotifyOperatorActionHandler
    implements RequestTaskActionHandler<EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;
    private final EmpIssuanceCorsiaReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        AppUser appUser, EmpIssuanceCorsiaNotifyOperatorForDecisionRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        reviewNotifyOperatorValidatorService.validate(requestTask, payload, appUser);

        DecisionNotification decisionNotification = payload.getDecisionNotification();
        requestEmpCorsiaReviewService.saveDecisionNotification(requestTask, decisionNotification, appUser);

        // complete task
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload =
            (EmpIssuanceCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        EmpIssuanceDeterminationType determinationType = taskPayload.getDetermination().getType();
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, determinationType,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
