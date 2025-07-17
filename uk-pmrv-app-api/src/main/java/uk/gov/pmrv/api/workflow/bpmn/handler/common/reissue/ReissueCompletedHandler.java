package uk.gov.pmrv.api.workflow.bpmn.handler.common.reissue;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@RequiredArgsConstructor
public abstract class ReissueCompletedHandler implements JavaDelegate {

	private final ReissueCompletedService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final boolean reissueSucceeded = (boolean) execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED);
		
		service.reissueCompleted(requestId, accountId, reissueSucceeded);
		
		//increment completed number var
		final Integer numberOfAccountsCompleted = (Integer) execution.getVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED);
		execution.setVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
	}

}