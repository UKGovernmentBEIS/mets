package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.vir;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirSendReminderNotificationService;

@ExtendWith(MockitoExtension.class)
class AviationVirRespondSecondReminderDateReachedHandlerFlowableTest {

    @InjectMocks
    private AviationVirRespondSecondReminderDateReachedHandlerFlowable handler;

    @Mock
    private VirSendReminderNotificationService virSendReminderNotificationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";
        final Date date = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AVIATION_VIR_EXPIRATION_DATE)).thenReturn(date);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(virSendReminderNotificationService, times(1)).sendRespondSecondReminderNotification(requestId, date);
    }
}
