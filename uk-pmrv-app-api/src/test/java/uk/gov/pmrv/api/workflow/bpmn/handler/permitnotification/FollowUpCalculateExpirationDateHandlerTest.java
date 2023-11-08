package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

@ExtendWith(MockitoExtension.class)
class FollowUpCalculateExpirationDateHandlerTest {

	@InjectMocks
    private FollowUpCalculateExpirationDateHandler cut;

	@Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;
	
	@Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;
	
	@Mock
	private DelegateExecution execution;

    @Test
    void execute() {
    	String requestId = "1";
    	Date expirationDate = new Date();
    	Map<String, Object> expirationVars = Map.of(
                "var1", "val1"
                );
    	
    	when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	when(reviewSubmittedService.resolveFollowUpExpirationDate(requestId)).thenReturn(expirationDate);
    	when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate)).thenReturn(expirationVars);
    	
    	cut.execute(execution);
    	
    	verify(execution, times(1)).setVariables(expirationVars);
    	verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
    	verify(reviewSubmittedService, times(1)).resolveFollowUpExpirationDate(requestId);
    	verify(requestExpirationVarsBuilder, times(1)).buildExpirationVars(RequestExpirationType.FOLLOW_UP_RESPONSE, expirationDate);
    }
}
