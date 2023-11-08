package uk.gov.pmrv.api.workflow.request.flow.common.taskhandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskCreateService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class DefaultUserTaskCreatedHandlerTest {

	@InjectMocks
    private DefaultUserTaskCreatedHandler cut;

    @Mock
    private RequestTaskCreateService requestTaskCreateService;
    
    @Test
    void createRequestTask_dynamic_default_request_type() {
    	String requestId = "1";
    	String processTaskId = "proc";
    	DynamicUserTaskDefinitionKey taskDefinitionKey = DynamicUserTaskDefinitionKey.APPLICATION_REVIEW;
    	Map<String, Object> variables = Map.of(
    			BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_ISSUANCE.name()
    			);
    	
    	cut.createRequestTask(requestId, processTaskId, taskDefinitionKey.name(), variables);
    	
    	verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW, null, null);
    }
    
    @Test
    void createRequestTask_dynamic_custom_request_type() {
    	String requestId = "1";
    	String processTaskId = "proc";
    	DynamicUserTaskDefinitionKey taskDefinitionKey = DynamicUserTaskDefinitionKey.APPLICATION_PEER_REVIEW;
    	Map<String, Object> variables = Map.of(
    			BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_VARIATION.name(),
    			BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.PERMIT_VARIATION_REGULATOR_LED.getCode()
    			);
    	
    	cut.createRequestTask(requestId, processTaskId, taskDefinitionKey.name(), variables);
    	
    	verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW);
    }
    
    @Test
    void createRequestTask_fixed_request_task_type() {
    	String requestId = "1";
    	String processTaskId = "proc";
    	String taskDefinitionKey = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name();
    	Map<String, Object> variables = Map.of(
    			BpmnProcessConstants.REQUEST_TYPE, RequestType.PERMIT_ISSUANCE.name()
    			);
    	
    	cut.createRequestTask(requestId, processTaskId, taskDefinitionKey, variables);
    	
    	verify(requestTaskCreateService, times(1)).create(requestId, processTaskId, RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT);
    }
}
