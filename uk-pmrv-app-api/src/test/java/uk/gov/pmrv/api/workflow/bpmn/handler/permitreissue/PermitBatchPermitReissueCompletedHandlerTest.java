package uk.gov.pmrv.api.workflow.bpmn.handler.permitreissue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@ExtendWith(MockitoExtension.class)
class PermitBatchPermitReissueCompletedHandlerTest {

	@InjectMocks
    private PermitBatchPermitReissueCompletedHandler cut;

    @Mock
    private ReissueCompletedService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        String permitReissueRequestId = "2";
        Long accountId = 3L;
        boolean reissueSucceeded = true;
        Integer numberOfAccountsCompleted = 0;
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_ID)).thenReturn(permitReissueRequestId);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED)).thenReturn(reissueSucceeded);
        when(execution.getVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED)).thenReturn(numberOfAccountsCompleted);

        cut.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REISSUE_REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.BATCH_NUMBER_OF_ACCOUNTS_COMPLETED, 1);
        verify(service, times(1)).reissueCompleted(requestId, accountId, permitReissueRequestId, reissueSucceeded);
    }
    
}
