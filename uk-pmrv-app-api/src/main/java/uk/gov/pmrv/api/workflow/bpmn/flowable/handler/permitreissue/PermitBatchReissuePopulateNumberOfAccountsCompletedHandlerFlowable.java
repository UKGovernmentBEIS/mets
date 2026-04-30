package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitreissue;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.service.PermitBatchReissueQueryService;

@Log4j2
@Service
@RequiredArgsConstructor
public class PermitBatchReissuePopulateNumberOfAccountsCompletedHandlerFlowable implements JavaDelegate {

	private final PermitBatchReissueQueryService permitBatchReissueQueryService;

    @Override
    public void execute(DelegateExecution execution) {
    	final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
    	final long accountsCompletedNumber = permitBatchReissueQueryService.getNumberOfAccountsCompleted(requestId);
    	execution.setVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, accountsCompletedNumber);
    }
}
