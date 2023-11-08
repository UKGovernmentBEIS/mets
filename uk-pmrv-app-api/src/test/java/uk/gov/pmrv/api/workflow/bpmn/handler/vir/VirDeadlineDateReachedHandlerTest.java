package uk.gov.pmrv.api.workflow.bpmn.handler.vir;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.vir.service.VirDeadlineService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VirDeadlineDateReachedHandlerTest {

    @InjectMocks
    private VirDeadlineDateReachedHandler handler;

    @Mock
    private VirDeadlineService virDeadlineService;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";
        final DelegateExecution execution = mock(DelegateExecution.class);

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(virDeadlineService, times(1)).sendDeadlineNotification(requestId);
    }
}
