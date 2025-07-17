package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.audit;

import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditExpirationDateService;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditOperatorRespondSecondReminderDateReachedHandlerTest {

    @InjectMocks
    private InstallationAuditOperatorRespondSecondReminderDateReachedHandler handler;

    @Mock
    private InstallationAuditExpirationDateService installationAuditExpirationDateService;

    @Test
    void execute() throws Exception {

        final String requestId = "INS00045-2024-2";
        final DelegateExecution execution = mock(DelegateExecution.class);
        final Date expirationDate = DateUtils.addDays(new Date(), 10);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.INSTALLATION_AUDIT_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.INSTALLATION_AUDIT_EXPIRATION_DATE);
        verify(installationAuditExpirationDateService, times(1))
            .sendRespondSecondReminderNotification(requestId, expirationDate);
    }
}
