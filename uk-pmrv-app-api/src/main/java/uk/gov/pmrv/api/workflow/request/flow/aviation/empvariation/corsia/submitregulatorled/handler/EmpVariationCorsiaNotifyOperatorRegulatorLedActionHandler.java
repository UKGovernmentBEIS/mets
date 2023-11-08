package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaSubmitRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator.EmpVariationCorsiaNotifyOperatorRegulatorLedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;


@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaNotifyOperatorRegulatorLedActionHandler 
	implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final EmpVariationCorsiaSubmitRegulatorLedService submitRegulatorLedService;
    private final EmpVariationCorsiaNotifyOperatorRegulatorLedValidator validator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        validator.validate(requestTask, payload, pmrvUser);

        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        submitRegulatorLedService.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
        
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
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED);
    }
}
