package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitsurrender;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderNoticeDateReminderService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderNoticeDateReminderReachedHandlerFlowableTest {

	@InjectMocks
    private PermitSurrenderNoticeDateReminderReachedHandlerFlowable handler;
    
    @Mock
    private PermitSurrenderNoticeDateReminderService permitSurrenderNoticeDateReminderService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	final String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(permitSurrenderNoticeDateReminderService, times(1)).sendNoticeDateReminder(requestId);
    }
}
