package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.message;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.common.engine.impl.el.FixedValue;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.bpmn.flowable.FlowableWorkflowService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class MsgThrowHandlerFlowableTest {

	@InjectMocks
    private MsgThrowHandlerFlowable cut;

    @Mock
    private DelegateExecution execution;

    @Mock
    private FlowableWorkflowService flowableWorkflowService;

    @Test
    void execute() throws Exception {
    	String messageNameStr = "eventMessage1";
    	cut.setMessageName(new FixedValue(messageNameStr));
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        cut.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(flowableWorkflowService, times(1)).sendEvent(requestId, messageNameStr);
    }
    
}
