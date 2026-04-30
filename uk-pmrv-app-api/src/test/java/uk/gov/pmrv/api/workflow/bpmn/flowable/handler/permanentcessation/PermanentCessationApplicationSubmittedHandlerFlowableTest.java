package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permanentcessation;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationSubmittedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermanentCessationApplicationSubmittedHandlerFlowableTest {

    @Mock
    private PermanentCessationSubmittedService permanentCessationSubmittedService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private PermanentCessationApplicationSubmittedHandlerFlowable handler;

    @Test
    void execute_shouldCallSubmit_withRequestId() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(permanentCessationSubmittedService, times(1)).submit(requestId);
        verifyNoMoreInteractions(permanentCessationSubmittedService);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
    }

    @Test
    void execute_shouldCallSubmit_withNull_whenRequestIdMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(permanentCessationSubmittedService, times(1)).submit(null);
        verifyNoMoreInteractions(permanentCessationSubmittedService);
    }
}
