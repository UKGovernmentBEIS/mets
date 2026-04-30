package uk.gov.pmrv.api.workflow.bpmn.flowable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlowableWorkflowServiceTest {
	
	@InjectMocks
    private FlowableWorkflowService workflowService;
	
	@Mock
	private RuntimeService runtimeService;
	
	@Mock
    private TaskService taskService;
	
	@Test
	void completeTask() {
		final String processTaskId = "1";
		final Map<String, Object> vars = Map.of("test1", "val1");
		
		//invoke
		workflowService.completeTask(processTaskId, vars);
	    
	    //verify
	    verify(taskService, times(1)).complete(processTaskId, vars);
	}
	
	@Test
	void getProcessInstanceIdByBusinessKey() {
		final String businessKey = "1";
		
		ProcessInstanceQuery eqMock1 = Mockito.mock(ProcessInstanceQuery.class);
		ProcessInstanceQuery eqMock2 = Mockito.mock(ProcessInstanceQuery.class);
		ProcessInstance processInstanceMock = Mockito.mock(ProcessInstance.class);
		
		when(runtimeService.createProcessInstanceQuery()).thenReturn(eqMock1);
		when(eqMock1.processInstanceBusinessKey(businessKey)).thenReturn(eqMock2);
		when(eqMock2.singleResult()).thenReturn(processInstanceMock);
		when(processInstanceMock.getProcessInstanceId()).thenReturn("ExpectedId");
		
		//invoke
		String result = workflowService.getProcessInstanceIdByBusinessKey(businessKey);
		
		assertThat(result).isEqualTo("ExpectedId");
		
		verify(runtimeService, times(1)).createProcessInstanceQuery();
		verify(eqMock1, times(1)).processInstanceBusinessKey(businessKey);
		verify(eqMock2, times(1)).singleResult();
		verify(processInstanceMock, times(1)).getProcessInstanceId();
	    
	}

}
