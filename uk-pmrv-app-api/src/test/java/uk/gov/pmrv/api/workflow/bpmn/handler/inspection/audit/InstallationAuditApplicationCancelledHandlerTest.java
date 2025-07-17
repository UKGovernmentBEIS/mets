package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.audit;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmitService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditApplicationCancelledHandlerTest {

    @InjectMocks
    private InstallationAuditApplicationCancelledHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private InstallationAuditSubmitService installationAuditSubmitService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(installationAuditSubmitService, times(1)).cancel(requestId);
    }
}
