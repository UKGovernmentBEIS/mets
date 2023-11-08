package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import org.apache.commons.lang3.time.DateUtils;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirSendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirRespondFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private VirRespondFirstReminderDateReachedHandler handler;

    @Mock
    private VirSendReminderNotificationService virSendReminderNotificationService;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";
        final DelegateExecution execution = mock(DelegateExecution.class);
        final Date expirationDate = DateUtils.addDays(new Date(), 10);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.VIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.VIR_EXPIRATION_DATE);
        verify(virSendReminderNotificationService, times(1))
                .sendRespondFirstReminderNotification(requestId, expirationDate);
    }
}
