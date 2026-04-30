package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationFollowUpSendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpResponseSecondReminderDateReachedHandlerFlowableTest {

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private PermitNotificationFollowUpSendReminderNotificationService sendReminderNotificationService;

    @InjectMocks
    private FollowUpResponseSecondReminderDateReachedHandlerFlowable handler;

    @Test
    void execute_callsServiceWithCorrectVariables() {
        String requestId = "REQ-123";
        Date expirationDate = new Date();

        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID))
                .thenReturn(requestId);
        when(delegateExecution.getVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_EXPIRATION_DATE))
                .thenReturn(expirationDate);
        handler.execute(delegateExecution);

        verify(sendReminderNotificationService)
                .sendSecondReminderNotification(requestId, expirationDate);

        verifyNoMoreInteractions(sendReminderNotificationService);
    }
}
