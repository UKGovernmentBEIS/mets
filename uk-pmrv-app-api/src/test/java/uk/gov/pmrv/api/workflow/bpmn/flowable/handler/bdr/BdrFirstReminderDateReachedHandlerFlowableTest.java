package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRSendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BdrFirstReminderDateReachedHandlerFlowableTest {

    @Mock
    private BDRSendReminderNotificationService bdrSendReminderNotificationService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private BdrFirstReminderDateReachedHandlerFlowable handler;

    @Test
    void execute_callsServiceWithCorrectVariables() {
        String requestId = "REQ-123";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.BDR_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(bdrSendReminderNotificationService)
                .sendFirstReminderNotification(requestId, expirationDate);
        verifyNoMoreInteractions(bdrSendReminderNotificationService);
    }
}
