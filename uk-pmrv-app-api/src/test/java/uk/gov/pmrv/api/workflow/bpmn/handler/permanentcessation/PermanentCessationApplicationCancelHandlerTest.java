package uk.gov.pmrv.api.workflow.bpmn.handler.permanentcessation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationApplicationCancelHandlerTest {

    @InjectMocks
    private PermanentCessationApplicationCancelHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermanentCessationService submitService;

    @Test
    void execute() throws Exception {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(submitService, times(1)).cancel(requestId);
    }
}
