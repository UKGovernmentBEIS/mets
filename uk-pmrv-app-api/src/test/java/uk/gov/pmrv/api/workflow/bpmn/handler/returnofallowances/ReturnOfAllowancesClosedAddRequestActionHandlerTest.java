package uk.gov.pmrv.api.workflow.bpmn.handler.returnofallowances;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesApplicationCancelledService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesClosedAddRequestActionHandlerTest {

    @Mock
    private ReturnOfAllowancesApplicationCancelledService service;

    @InjectMocks
    private ReturnOfAllowancesAddCancelledRequestActionHandler actionHandler;

    @Test
    void execute() throws Exception {
        String requestId = "request123";
        DelegateExecution execution = Mockito.mock(DelegateExecution.class);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        actionHandler.execute(execution);

        verify(service).cancel(requestId);
    }

}