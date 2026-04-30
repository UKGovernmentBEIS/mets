package uk.gov.pmrv.api.workflow.bpmn.flowable.listener;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.common.engine.api.delegate.event.FlowableEntityEvent;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.application.taskcompleted.RequestTaskCompleteService;

@ExtendWith(MockitoExtension.class)
class UserTaskCompletedListenerFlowableTest {

	@InjectMocks
	private UserTaskCompletedListenerFlowable userTaskCompletedListener;
	
	@Mock
	private RequestTaskCompleteService requestTaskCompleteService;
	
	@Mock
    private FlowableEntityEvent event;
	
	@Test
	void onEvent() {
		TaskEntity entity = Mockito.mock(TaskEntity.class);
    	when(event.getEntity()).thenReturn(entity);
		
		final String taskId ="taskid";
		when(entity.getId()).thenReturn(taskId);
		
		//invoke
		userTaskCompletedListener.onEvent(event);
		
		//verify
		verify(event, times(1)).getEntity();
		verify(entity, times(1)).getId();
		verify(requestTaskCompleteService, times(1)).complete(taskId);
	}
}
