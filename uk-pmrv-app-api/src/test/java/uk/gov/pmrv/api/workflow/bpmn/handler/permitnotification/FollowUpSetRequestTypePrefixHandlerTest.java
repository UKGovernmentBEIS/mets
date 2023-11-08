package uk.gov.pmrv.api.workflow.bpmn.handler.permitnotification;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class FollowUpSetRequestTypePrefixHandlerTest {

	@InjectMocks
    private FollowUpSetRequestTypePrefixHandler cut;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	// Invoke
        cut.execute(execution);

        // Verify
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.PERMIT_NOTIFICATION_FOLLOW_UP.getCode());
    }
}
