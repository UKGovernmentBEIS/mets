package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRSendReminderNotificationService;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class WasteQDRSecondReminderDateReachedHandlerTest {

    @Mock
    private WasteQDRSendReminderNotificationService wasteQDRSendReminderNotificationService;

    @InjectMocks
    private WasteQDRSecondReminderDateReachedHandler wasteQDRSecondReminderDateReachedHandler;

    @Test
    public void execute() throws Exception {
        // Arrange
        DelegateExecution execution = mock(DelegateExecution.class);
        String requestId = "request-id";
        Date expirationDate = new Date();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE)).thenReturn(expirationDate);

        wasteQDRSecondReminderDateReachedHandler.execute(execution);

        verify(wasteQDRSendReminderNotificationService).sendSecondReminderNotification(requestId, expirationDate);
    }
}
