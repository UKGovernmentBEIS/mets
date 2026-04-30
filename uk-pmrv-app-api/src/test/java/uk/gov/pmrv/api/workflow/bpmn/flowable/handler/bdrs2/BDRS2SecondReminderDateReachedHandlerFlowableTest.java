package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2SendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2SecondReminderDateReachedHandlerFlowableTest {

    @InjectMocks
    private BDRS2SecondReminderDateReachedHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private BDRS2SendReminderNotificationService bdrs2SendReminderNotificationService;

    @Test
    void execute() {
        String requestId = "BDRS2-00001-2025";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.BDRS2_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(bdrs2SendReminderNotificationService).sendSecondReminderNotification(requestId, expirationDate);
    }
}
