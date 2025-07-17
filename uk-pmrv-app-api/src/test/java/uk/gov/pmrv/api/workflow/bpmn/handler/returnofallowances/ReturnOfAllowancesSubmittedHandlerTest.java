package uk.gov.pmrv.api.workflow.bpmn.handler.returnofallowances;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesSubmittedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesSubmittedHandlerTest {

    @Mock
    private ReturnOfAllowancesSubmittedService service;

    @InjectMocks
    private ReturnOfAllowancesSubmittedHandler handler;

    @Test
    void execute() {
        String requestId = "request123";
        DelegateExecution execution = Mockito.mock(DelegateExecution.class);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).submit(requestId);
    }
}
