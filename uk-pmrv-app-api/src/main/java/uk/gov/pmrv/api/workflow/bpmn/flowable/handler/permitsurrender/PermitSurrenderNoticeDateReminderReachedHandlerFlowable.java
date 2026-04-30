package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderNoticeDateReminderService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderNoticeDateReminderReachedHandlerFlowable implements JavaDelegate {
	
	private final PermitSurrenderNoticeDateReminderService permitSurrenderNoticeDateReminderService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		permitSurrenderNoticeDateReminderService.sendNoticeDateReminder(requestId);
	}

}
