package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.service.EmpVariationUkEtsSubmitRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator.EmpVariationUkEtsNotifyOperatorRegulatorLedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;


@Component
@RequiredArgsConstructor
public class EmpVariationUkEtsNotifyOperatorRegulatorLedActionHandler 
	implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationUkEtsSubmitRegulatorLedService empVariationUkEtsSubmitRegulatorLedService;
    private final EmpVariationUkEtsNotifyOperatorRegulatorLedValidator validator;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType,
                        PmrvUser pmrvUser, NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        validator.validate(requestTask, payload, pmrvUser);

        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        empVariationUkEtsSubmitRegulatorLedService.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
        
		requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        // complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
            	BpmnProcessConstants.EMP_VARIATION_SUBMIT_OUTCOME, EmpVariationSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED);
    }
}
