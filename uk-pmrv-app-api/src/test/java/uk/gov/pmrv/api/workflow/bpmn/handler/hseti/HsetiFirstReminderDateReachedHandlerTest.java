package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETISendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HsetiFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private HsetiFirstReminderDateReachedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private HSETISendReminderNotificationService hsetiSendReminderNotificationService;

    @Test
    void execute() throws Exception {

        String requestId = "HSETI00001-2021_2025";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.HSE_TI_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(hsetiSendReminderNotificationService).sendFirstReminderNotification(requestId, expirationDate);
    }
}
