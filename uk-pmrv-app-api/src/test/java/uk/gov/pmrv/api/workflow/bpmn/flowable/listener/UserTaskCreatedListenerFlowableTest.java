package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.CustomUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DefaultUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler.SystemMessageNotificationCreatedHandler;

@ExtendWith(MockitoExtension.class)
class UserTaskCreatedListenerFlowableTest {

	@InjectMocks
	private UserTaskCreatedListenerFlowable cut;
	
	@Spy
    private ArrayList<CustomUserTaskCreatedHandler> customUserTaskCreatedHandler;
	
	@Mock
	private DefaultUserTaskCreatedHandler defaultUserTaskCreatedHandler;
	
	@Mock
	private SystemMessageNotificationCreatedHandler systemMessageNotificationCreatedHandler;
	
	@Mock
    private FlowableEntityEvent event;
	
	@BeforeEach
    void setUp() {
		customUserTaskCreatedHandler.add(systemMessageNotificationCreatedHandler);
    }

	@Test
	void onEvent_default_handler() {
		TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
    	when(event.getEntity()).thenReturn(taskEntity);
		
		final String requestId = "1";
		final String processTaskId ="taskid";
		final String taskDefinitionKey = DynamicUserTaskDefinitionKey.APPLICATION_REVIEW.name();
		final Map<String, Object> variables = Map.of("var1", "val1");
		
		when(taskEntity.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskEntity.getId()).thenReturn(processTaskId);
		when(taskEntity.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		when(taskEntity.getVariables()).thenReturn(variables);
		
		when(systemMessageNotificationCreatedHandler.getTaskDefinitionKey())
			.thenReturn(DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name());
		
		// Invoke
		cut.onEvent(event);

		// Verify
		verify(taskEntity, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(taskEntity, times(1)).getId();
		verify(taskEntity, times(1)).getTaskDefinitionKey();
		verify(taskEntity, times(1)).getVariables();
		verify(systemMessageNotificationCreatedHandler, times(1)).getTaskDefinitionKey();
		verify(defaultUserTaskCreatedHandler, times(1)).createRequestTask(
				requestId, processTaskId, taskDefinitionKey, variables);
	}
	
	@Test
	void onTaskCreatedEvent_custom_handler() {
		TaskEntity taskEntity = Mockito.mock(TaskEntity.class);
    	when(event.getEntity()).thenReturn(taskEntity);
		
		final String requestId = "1";
		final String processTaskId ="taskid";
		final String taskDefinitionKey = DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name();
		final Map<String, Object> variables = Map.of("var1", "val1");
		
		when(taskEntity.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskEntity.getId()).thenReturn(processTaskId);
		when(taskEntity.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		when(taskEntity.getVariables()).thenReturn(variables);
		
		when(systemMessageNotificationCreatedHandler.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		
		// Invoke
		cut.onEvent(event);

		// Verify
		verify(taskEntity, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(taskEntity, times(1)).getId();
		verify(taskEntity, times(1)).getTaskDefinitionKey();
		verify(taskEntity, times(1)).getVariables();
		verify(systemMessageNotificationCreatedHandler, times(1)).getTaskDefinitionKey();
		
		verify(systemMessageNotificationCreatedHandler, times(1)).createRequestTask(
				requestId, processTaskId, taskDefinitionKey, variables);
		
		verifyNoInteractions(defaultUserTaskCreatedHandler);
	}
	

}
