package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.vir;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirSendReminderNotificationService;

@ExtendWith(MockitoExtension.class)
class AviationVirRespondSecondReminderDateReachedHandlerTest {

    @InjectMocks
    private AviationVirRespondSecondReminderDateReachedHandler handler;

    @Mock
    private VirSendReminderNotificationService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        
        final String requestId = "1";
        
        final Date date = new Date(); 
        
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AVIATION_VIR_EXPIRATION_DATE)).thenReturn(date);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).sendRespondSecondReminderNotification(requestId, date);
    }
}
