package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

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
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCreateRequestService;

@ExtendWith(MockitoExtension.class)
class EmpBatchTriggerEmpReissueHandlerTest {

	@InjectMocks
    private EmpBatchTriggerEmpReissueHandler cut;

    @Mock
    private ReissueCreateRequestService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	Long accountId = 1L;
        String requestId = "2";
        String requestBusinessKey = "3";
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);

        cut.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(service, times(1)).createReissueRequest(accountId, requestId, requestBusinessKey);
    }
    
}
