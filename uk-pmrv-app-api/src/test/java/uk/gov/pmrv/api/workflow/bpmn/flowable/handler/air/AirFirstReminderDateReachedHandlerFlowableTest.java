package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirSendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirFirstReminderDateReachedHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private AirSendReminderNotificationService service;

    @InjectMocks
    private AirFirstReminderDateReachedHandlerFlowable handler;

    @Test
    void execute_callsSendFirstReminderNotification() {
        String requestId = "REQ-1";
        Date expirationDate = new Date();
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AIR_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(service).sendFirstReminderNotification(requestId, expirationDate);
    }
}
