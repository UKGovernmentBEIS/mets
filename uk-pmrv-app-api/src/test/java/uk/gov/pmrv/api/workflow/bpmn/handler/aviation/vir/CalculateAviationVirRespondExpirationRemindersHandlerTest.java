package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.vir;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.CalculateRespondToRegulatorCommentsExpirationDateService;

@ExtendWith(MockitoExtension.class)
class CalculateAviationVirRespondExpirationRemindersHandlerTest {

    @InjectMocks
    private CalculateAviationVirRespondExpirationRemindersHandler handler;

    @Mock
    private CalculateRespondToRegulatorCommentsExpirationDateService calculateRespondToRegulatorCommentsExpirationDateService;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Test
    void execute() throws Exception {
        
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "AEM-001";
        final Date expirationDate = new Date();
        final Map<String, Object> vars = Map.of(
                "var1", "val1"
        );

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(calculateRespondToRegulatorCommentsExpirationDateService.calculateExpirationDate(requestId))
                .thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AVIATION_VIR, expirationDate))
                .thenReturn(vars);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(calculateRespondToRegulatorCommentsExpirationDateService, times(1))
                .calculateExpirationDate(requestId);
        verify(requestExpirationVarsBuilder, times(1))
                .buildExpirationVars(RequestExpirationType.AVIATION_VIR, expirationDate);
        verify(execution, times(1)).setVariables(vars);
    }
}
