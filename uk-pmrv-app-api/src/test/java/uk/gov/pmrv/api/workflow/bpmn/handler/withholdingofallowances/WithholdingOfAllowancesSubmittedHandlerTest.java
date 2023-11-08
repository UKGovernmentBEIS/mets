package uk.gov.pmrv.api.workflow.bpmn.handler.withholdingofallowances;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service.WithholdingOfAllowancesSubmittedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WithholdingOfAllowancesSubmittedHandlerTest {

    @Mock
    private WithholdingOfAllowancesSubmittedService service;

    @InjectMocks
    private WithholdingOfAllowancesSubmittedHandler handler;

    @Test
    public void execute_ShouldSubmitRequest() {
        String requestId = "request123";
        DelegateExecution execution = Mockito.mock(DelegateExecution.class);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).submit(requestId);
    }
}
