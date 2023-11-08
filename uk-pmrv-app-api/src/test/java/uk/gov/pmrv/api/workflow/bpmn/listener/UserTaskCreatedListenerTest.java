package uk.gov.pmrv.api.workflow.bpmn.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.CustomUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DefaultUserTaskCreatedHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.handler.SystemMessageNotificationCreatedHandler;

@ExtendWith(MockitoExtension.class)
class UserTaskCreatedListenerTest {

	@InjectMocks
	private UserTaskCreatedListener cut;
	
	@Spy
    private ArrayList<CustomUserTaskCreatedHandler> customUserTaskCreatedHandler;
	
	@Mock
	private DefaultUserTaskCreatedHandler defaultUserTaskCreatedHandler;
	
	@Mock
	private SystemMessageNotificationCreatedHandler systemMessageNotificationCreatedHandler;
	
	@Mock
	private DelegateTask taskDelegate;
	
	@BeforeEach
    void setUp() {
		customUserTaskCreatedHandler.add(systemMessageNotificationCreatedHandler);
    }

	@Test
	void onTaskCreatedEvent_default_handler() {
		final String requestId = "1";
		final String processTaskId ="taskid";
		final String taskDefinitionKey = DynamicUserTaskDefinitionKey.APPLICATION_REVIEW.name();
		final Map<String, Object> variables = Map.of("var1", "val1");
		
		when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskDelegate.getId()).thenReturn(processTaskId);
		when(taskDelegate.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		when(taskDelegate.getVariables()).thenReturn(variables);
		
		when(systemMessageNotificationCreatedHandler.getTaskDefinitionKey())
			.thenReturn(DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name());
		
		// Invoke
		cut.onTaskCreatedEvent(taskDelegate);

		// Verify
		verify(taskDelegate, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(taskDelegate, times(1)).getId();
		verify(taskDelegate, times(1)).getTaskDefinitionKey();
		verify(taskDelegate, times(1)).getVariables();
		verify(systemMessageNotificationCreatedHandler, times(1)).getTaskDefinitionKey();
		verify(defaultUserTaskCreatedHandler, times(1)).createRequestTask(
				requestId, processTaskId, taskDefinitionKey, variables);
	}
	
	@Test
	void onTaskCreatedEvent_custom_handler() {
		final String requestId = "1";
		final String processTaskId ="taskid";
		final String taskDefinitionKey = DynamicUserTaskDefinitionKey.SYSTEM_MESSAGE_NOTIFICATION_TASK.name();
		final Map<String, Object> variables = Map.of("var1", "val1");
		
		when(taskDelegate.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(taskDelegate.getId()).thenReturn(processTaskId);
		when(taskDelegate.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		when(taskDelegate.getVariables()).thenReturn(variables);
		
		when(systemMessageNotificationCreatedHandler.getTaskDefinitionKey()).thenReturn(taskDefinitionKey);
		
		// Invoke
		cut.onTaskCreatedEvent(taskDelegate);

		// Verify
		verify(taskDelegate, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
		verify(taskDelegate, times(1)).getId();
		verify(taskDelegate, times(1)).getTaskDefinitionKey();
		verify(taskDelegate, times(1)).getVariables();
		verify(systemMessageNotificationCreatedHandler, times(1)).getTaskDefinitionKey();
		
		verify(systemMessageNotificationCreatedHandler, times(1)).createRequestTask(
				requestId, processTaskId, taskDefinitionKey, variables);
		
		verifyNoInteractions(defaultUserTaskCreatedHandler);
	}
	

}
