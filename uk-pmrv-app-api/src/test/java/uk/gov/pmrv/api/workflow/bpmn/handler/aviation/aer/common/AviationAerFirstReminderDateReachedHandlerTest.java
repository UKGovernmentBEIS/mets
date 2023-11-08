package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.common;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerSendReminderNotificationService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private AviationAerFirstReminderDateReachedHandler firstReminderDateReachedHandler;

    @Mock
    private AviationAerSendReminderNotificationService sendReminderNotificationService;

    @Test
    void execute() throws Exception {
        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        String requestId = "REQ-001";
        Date expirationDate = new Date();

        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(delegateExecution.getVariable(BpmnProcessConstants.AVIATION_AER_EXPIRATION_DATE)).thenReturn(expirationDate);

        firstReminderDateReachedHandler.execute(delegateExecution);

        verify(sendReminderNotificationService).sendFirstReminderNotification(requestId, expirationDate);
    }
}