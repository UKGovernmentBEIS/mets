package uk.gov.pmrv.api.workflow.bpmn.handler.air;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirSendReminderNotificationService;

@ExtendWith(MockitoExtension.class)
class AirSecondReminderDateReachedHandlerTest {

    @InjectMocks
    private AirSecondReminderDateReachedHandler handler;

    @Mock
    private AirSendReminderNotificationService sendReminderNotificationService;

    @Test
    void execute() throws Exception {
        
        final String requestId = "requestId";
        final DelegateExecution execution = mock(DelegateExecution.class);
        final Date expirationDate = DateUtils.addDays(new Date(), 10);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE);
        verify(sendReminderNotificationService, times(1))
                .sendSecondReminderNotification(requestId, expirationDate);
    }
}
