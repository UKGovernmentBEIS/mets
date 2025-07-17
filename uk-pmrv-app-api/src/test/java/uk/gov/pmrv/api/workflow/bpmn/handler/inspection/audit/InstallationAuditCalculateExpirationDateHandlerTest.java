package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.audit;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditExpirationDateService;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditCalculateExpirationDateHandlerTest {

    @InjectMocks
    private InstallationAuditCalculateExpirationDateHandler handler;

    @Mock
    private  RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private  InstallationAuditExpirationDateService installationAuditExpirationDateService;

    @Test
    void execute() throws Exception {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "INS00045-2024-2";
        final Date expirationDate = new Date();
        final Map<String, Object> vars = Map.of();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(installationAuditExpirationDateService.calculateExpirationDate(requestId))
                .thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.INSTALLATION_AUDIT, expirationDate))
                .thenReturn(vars);

        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(installationAuditExpirationDateService, times(1))
                .calculateExpirationDate(requestId);
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(RequestExpirationType.INSTALLATION_AUDIT, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }

}
