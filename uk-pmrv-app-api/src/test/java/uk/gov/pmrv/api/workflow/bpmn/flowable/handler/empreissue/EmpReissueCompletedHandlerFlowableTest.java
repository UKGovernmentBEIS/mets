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

import uk.gov.pmrv.api.workflow.bpmn.flowable.FlowableWorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueCompletedService;

@ExtendWith(MockitoExtension.class)
class EmpReissueCompletedHandlerFlowableTest {

    @InjectMocks
    private EmpReissueCompletedHandlerFlowable cut;

    @Mock
    private FlowableWorkflowService workflowService;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private ReissueCompletedService reissueCompletedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String batchRequestBusinessKey = "bk1";
        String batchProcessInstanceId = "proc1";
        String batchRequestId = "req1";
        Long accountId = 100L;
        Boolean reissueSucceeded = true;

        Request batchRequest = Request.builder()
            .id(batchRequestId)
            .processInstanceId(batchProcessInstanceId)
            .build();

        when(execution.getVariable(BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY)).thenReturn(batchRequestBusinessKey);
        when(workflowService.getProcessInstanceIdByBusinessKey(batchRequestBusinessKey)).thenReturn(batchProcessInstanceId);
        when(requestQueryService.findByProcessInstanceId(batchProcessInstanceId)).thenReturn(batchRequest);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED)).thenReturn(reissueSucceeded);

        cut.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY);
        verify(workflowService, times(1)).getProcessInstanceIdByBusinessKey(batchRequestBusinessKey);
        verify(requestQueryService, times(1)).findByProcessInstanceId(batchProcessInstanceId);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED);
        verify(reissueCompletedService, times(1)).reissueCompleted(batchRequestId, accountId, true, true);
    }

    @Test
    void execute_reissueSucceeded_false() {
        String batchRequestBusinessKey = "bk1";
        String batchProcessInstanceId = "proc1";
        String batchRequestId = "req1";
        Long accountId = 100L;
        Boolean reissueSucceeded = false;

        Request batchRequest = Request.builder()
            .id(batchRequestId)
            .processInstanceId(batchProcessInstanceId)
            .build();

        when(execution.getVariable(BpmnProcessConstants.BATCH_REQUEST_BUSINESS_KEY)).thenReturn(batchRequestBusinessKey);
        when(workflowService.getProcessInstanceIdByBusinessKey(batchRequestBusinessKey)).thenReturn(batchProcessInstanceId);
        when(requestQueryService.findByProcessInstanceId(batchProcessInstanceId)).thenReturn(batchRequest);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED)).thenReturn(reissueSucceeded);

        cut.execute(execution);

        verify(reissueCompletedService, times(1)).reissueCompleted(batchRequestId, accountId, false, true);
    }
}
