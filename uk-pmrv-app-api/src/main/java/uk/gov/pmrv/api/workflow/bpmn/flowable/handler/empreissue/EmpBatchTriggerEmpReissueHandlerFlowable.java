package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCreateRequestService;

@Service
@RequiredArgsConstructor
public class EmpBatchTriggerEmpReissueHandlerFlowable implements JavaDelegate {

	private final ReissueCreateRequestService reissueCreateRequestService;

	@Override
	public void execute(DelegateExecution execution) {
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);
		reissueCreateRequestService.createReissueRequest(accountId, requestId, requestBusinessKey);
	}
}
