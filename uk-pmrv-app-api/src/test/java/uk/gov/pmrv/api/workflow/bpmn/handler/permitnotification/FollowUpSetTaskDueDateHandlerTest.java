package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.util.Date;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;

@ExtendWith(MockitoExtension.class)
class FollowUpSetTaskDueDateHandlerTest {

	@InjectMocks
    private FollowUpSetTaskDueDateHandler cut;

	@Mock
    private RequestTaskTimeManagementService requestTaskTimeManagementService;
	
	@Mock
	private DelegateExecution execution;

    @Test
    void execute() {
    	String requestId = "1";
    	Date expirationDate = new Date();
    	
    	when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	when(execution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE)).thenReturn(expirationDate);
    	
    	cut.execute(execution);
    	
    	verify(requestTaskTimeManagementService, times(1)).setDueDateToTasks(requestId, RequestExpirationType.FOLLOW_UP_RESPONSE,
    			expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    	verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
    	verify(execution, times(1)).getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE);
    }
}