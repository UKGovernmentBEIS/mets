package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.air;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationVarsBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.CalculateAirRespondToRegulatorCommentsExpirationDateService;

import java.util.Date;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAirRespondExpirationRemindersHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private RequestExpirationVarsBuilder requestExpirationVarsBuilder;

    @Mock
    private CalculateAirRespondToRegulatorCommentsExpirationDateService expirationDateService;

    @InjectMocks
    private CalculateAirRespondExpirationRemindersHandlerFlowable handler;

    @Test
    void execute_setsExpirationVarsOnExecution() {
        String requestId = "REQ-1";
        Date expirationDate = new Date();
        Map<String, Object> expirations = Map.of("airFirstReminderDate", expirationDate);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(expirationDateService.calculateExpirationDate(requestId)).thenReturn(expirationDate);
        when(requestExpirationVarsBuilder.buildExpirationVars(RequestExpirationType.AIR, expirationDate)).thenReturn(expirations);

        handler.execute(execution);

        verify(execution).setVariables(expirations);
    }
}
