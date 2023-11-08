package uk.gov.pmrv.api.workflow.bpmn.handler.returnofallowances;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesApplicationCancelledService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesAddCancelledRequestActionHandlerTest {

    @Mock
    private ReturnOfAllowancesApplicationCancelledService service;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private ReturnOfAllowancesAddCancelledRequestActionHandler handler;

    @Test
    void execute() throws Exception {
        String requestId = "123";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).cancel(requestId);
    }

}