package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class ProcessInstanceEndListenerFlowableTest {

    @InjectMocks
    private ProcessInstanceEndListenerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private FlowableEntityEvent event;

    @Test
    void onEvent() {
    	ExecutionEntity entity = Mockito.mock(ExecutionEntity.class);
    	
    	when(event.getEntity()).thenReturn(entity);
    	when(entity.hasVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(true);
    	
    	
        final String requestId = "1";
        when(entity.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        
        Boolean shouldBeDeleted = true;
        when(entity.getVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE)).thenReturn(true);
        
        final String processId = "1";
        when(entity.getProcessInstanceId()).thenReturn(processId);

        // Invoke
        handler.onEvent(event);

        // Verify
        verify(entity, times(1)).hasVariable(BpmnProcessConstants.REQUEST_ID);
        verify(entity, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(entity, times(1)).getVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE);
        verify(entity, times(1)).getProcessInstanceId();
        verify(requestService, times(1)).terminateRequest(requestId, processId, shouldBeDeleted);
    }

    
}
