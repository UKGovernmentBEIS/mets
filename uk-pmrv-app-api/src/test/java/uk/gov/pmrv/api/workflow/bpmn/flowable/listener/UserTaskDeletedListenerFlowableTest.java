package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;

import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandlerResolver;
import uk.gov.pmrv.api.workflow.request.application.taskdeleted.RequestTaskDeleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.rfi.handler.RfiWaitForResponseDeletedHandler;

@ExtendWith(MockitoExtension.class)
class UserTaskDeletedListenerFlowableTest {

    @InjectMocks
    private UserTaskDeletedListenerFlowable userTaskDeletedListener;
    
    @Mock
    private RequestTaskDeleteService requestTaskDeleteService;
    
    @Mock
    private DynamicUserTaskDeletedHandlerResolver dynamicUserTaskDeletedHandlerMapper;
    
    @Mock
    private RfiWaitForResponseDeletedHandler rfiWaitForResponseDeletedHandler;
    
    @Mock
    private FlowableEntityEvent event;
    
    @Test
    void onEvent_whenNoHandlerExists_thenDefaultDelete() {
    	TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
    	when(event.getEntity()).thenReturn(taskEntity);
    	
        final String processTaskId ="taskid";
        when(taskEntity.getId()).thenReturn(processTaskId);
        
        //invoke
        userTaskDeletedListener.onEvent(event);
        
        //verify
        verify(taskEntity, times(1)).getId();
        verify(requestTaskDeleteService, times(1)).delete(processTaskId);
    }

    @Test
    void onEvent_whenHandlerExists_thenHandlerDelete() {
    	TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
    	when(event.getEntity()).thenReturn(taskEntity);

        final String requestId = "1";
        final String processTaskId ="taskid";
        when(taskEntity.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(taskEntity.getId()).thenReturn(processTaskId);
        when(taskEntity.getVariables()).thenReturn(Map.of());
        when(taskEntity.getTaskDefinitionKey()).thenReturn(DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE.name());
        when(dynamicUserTaskDeletedHandlerMapper.get(DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE.name()))
            .thenReturn(Optional.of(rfiWaitForResponseDeletedHandler));

        // Invoke
        userTaskDeletedListener.onEvent(event);

        // Verify
        verify(taskEntity, times(1)).getTaskDefinitionKey();
        verify(rfiWaitForResponseDeletedHandler, times(1)).process(requestId, Map.of());
        verify(requestTaskDeleteService, times(1)).delete(processTaskId);
    }
}
