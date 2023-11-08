package uk.gov.pmrv.api.workflow.bpmn.handler.permitrevocation;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service.PermitRevokedService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitRevocationEffectiveDateReachedHandlerTest {

    @InjectMocks
    private PermitRevocationEffectiveDateReachedHandler handler;

    @Mock
    private PermitRevokedService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "1";
        final Date reportDate = new Date();
        Map<String, Object> variables = new HashMap<>(){{
            put(BpmnProcessConstants.AER_REQUIRED, true);
            put(BpmnProcessConstants.AER_EXPIRATION_DATE, reportDate);
        }};

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(service.getAerVariables(requestId)).thenReturn(variables);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.AER_REQUIRED, true);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.AER_EXPIRATION_DATE, reportDate);
    }
}
