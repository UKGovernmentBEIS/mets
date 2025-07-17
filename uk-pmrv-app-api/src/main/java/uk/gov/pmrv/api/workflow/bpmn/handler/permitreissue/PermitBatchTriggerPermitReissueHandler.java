package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCreateRequestService;

@Service
@RequiredArgsConstructor
public class PermitBatchTriggerPermitReissueHandler implements JavaDelegate {
	
	private final ReissueCreateRequestService reissueCreateRequestService;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);
		reissueCreateRequestService.createReissueRequest(accountId, requestId, requestBusinessKey);
	}

}
