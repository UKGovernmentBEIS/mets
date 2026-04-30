package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service.EmpBatchReissueQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpBatchReissuePopulateNumberOfAccountsCompletedHandlerFlowableTest {

	@InjectMocks
	private EmpBatchReissuePopulateNumberOfAccountsCompletedHandlerFlowable cut;

	@Mock
	private EmpBatchReissueQueryService empBatchReissueQueryService;

	@Mock
	private DelegateExecution execution;

	@Test
	void execute() {
		String requestId = "1";
		long completedCount = 3L;
		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(empBatchReissueQueryService.getNumberOfAccountsCompleted(requestId)).thenReturn(completedCount);

		cut.execute(execution);

		verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(empBatchReissueQueryService, times(1)).getNumberOfAccountsCompleted(requestId);
		verify(execution, times(1)).setVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, completedCount);
	}
}
