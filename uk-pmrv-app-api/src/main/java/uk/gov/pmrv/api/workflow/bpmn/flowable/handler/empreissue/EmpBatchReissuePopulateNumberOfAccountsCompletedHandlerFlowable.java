package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class EmpBatchReissuePopulateNumberOfAccountsCompletedHandlerFlowable implements JavaDelegate {

	private final EmpBatchReissueQueryService empBatchReissueQueryService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final long accountsCompletedNumber = empBatchReissueQueryService.getNumberOfAccountsCompleted(requestId);
		execution.setVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, accountsCompletedNumber);
	}
}
