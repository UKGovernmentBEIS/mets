package uk.gov.pmrv.api.workflow.bpmn.handler.inspection.onsiteinspection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionExpirationDateService;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionCalculateExpirationDateHandlerTest {

    @InjectMocks
    private InstallationOnsiteInspectionCalculateExpirationDateHandler handler;

    @Mock
    private  RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private InstallationOnsiteInspectionExpirationDateService installationOnsiteInspectionExpirationDateService;

    @Test
    void execute() throws Exception {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "INS00045-20";
        final Date expirationDate = new Date();
        final Map<String, Object> vars = Map.of();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(installationOnsiteInspectionExpirationDateService.calculateExpirationDate(requestId))
                .thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.INSTALLATION_ONSITE_INSPECTION, expirationDate))
                .thenReturn(vars);

        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(installationOnsiteInspectionExpirationDateService, times(1))
                .calculateExpirationDate(requestId);
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(RequestExpirationType.INSTALLATION_ONSITE_INSPECTION, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }

}
