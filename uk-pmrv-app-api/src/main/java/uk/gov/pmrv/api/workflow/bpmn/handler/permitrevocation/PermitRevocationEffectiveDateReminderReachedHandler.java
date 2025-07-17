package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.PermitRevocationEffectiveDateReminderService;

@Service
@RequiredArgsConstructor
public class PermitRevocationEffectiveDateReminderReachedHandler implements JavaDelegate {
	
	private final PermitRevocationEffectiveDateReminderService permitRevocationEffectiveDateReminderService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		permitRevocationEffectiveDateReminderService.sendEffectiveDateReminder(requestId);
	}

}
