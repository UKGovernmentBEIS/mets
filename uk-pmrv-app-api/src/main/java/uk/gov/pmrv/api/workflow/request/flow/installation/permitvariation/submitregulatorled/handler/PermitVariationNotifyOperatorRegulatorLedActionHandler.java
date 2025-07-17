package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation.PermitVariationNotifyOperatorRegulatorLedValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitVariationNotifyOperatorRegulatorLedActionHandler
		implements RequestTaskActionHandler<PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload> {
	
	private final RequestTaskService requestTaskService;
	private final PermitVariationNotifyOperatorRegulatorLedValidator validator;
	private final PermitVariationRegulatorLedService permitVariationRegulatorLedService;
	private final WorkflowService workflowService;
	
	@Override
	public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
			PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload payload) {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		
		// validate
		validator.validate(requestTask, payload, appUser);
		
		// save
		permitVariationRegulatorLedService.savePermitVariationDecisionNotificationRegulatorLed(requestTask, payload.getDecisionNotification(), appUser);
		
		 //set request's submission date
		requestTask.getRequest().setSubmissionDate(LocalDateTime.now());
		
		// complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
            		BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.SUBMITTED,
            		BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
        );
	}

	@Override
	public List<RequestTaskActionType> getTypes() {
		return List.of(RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED);
	}

}
