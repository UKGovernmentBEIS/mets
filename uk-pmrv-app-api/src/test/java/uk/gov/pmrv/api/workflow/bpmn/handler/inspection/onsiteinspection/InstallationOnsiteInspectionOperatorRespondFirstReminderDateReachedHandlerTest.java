package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;


import org.apache.commons.lang3.time.DateUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionOperatorRespondFirstReminderDateReachedHandlerTest {

    @InjectMocks
    private InstallationOnsiteInspectionOperatorRespondFirstReminderDateReachedHandler handler;

    @Mock
    private InstallationOnsiteInspectionExpirationDateService installationOnsiteInspectionExpirationDateService;


    @Test
    void execute() throws Exception {
        final String requestId = "INS00045-20";
        final DelegateExecution execution = mock(DelegateExecution.class);
        final Date expirationDate = DateUtils.addDays(new Date(), 10);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE)).thenReturn(expirationDate);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_EXPIRATION_DATE);
        verify(installationOnsiteInspectionExpirationDateService, times(1))
            .sendRespondFirstReminderNotification(requestId, expirationDate);
    }
}
