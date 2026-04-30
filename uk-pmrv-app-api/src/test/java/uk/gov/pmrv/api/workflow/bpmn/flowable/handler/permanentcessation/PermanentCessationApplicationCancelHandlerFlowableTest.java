package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permanentcessation;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermanentCessationApplicationCancelHandlerFlowableTest {

    @Mock
    private PermanentCessationService permanentCessationService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private PermanentCessationApplicationCancelHandlerFlowable handler;

    @Test
    void execute_shouldCallCancel_withRequestId() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(execution);
        verify(permanentCessationService, times(1)).cancel(requestId);
        verifyNoMoreInteractions(permanentCessationService);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
    }

    @Test
    void execute_shouldCallCancel_withNull_whenRequestIdMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(permanentCessationService, times(1)).cancel(null);
        verifyNoMoreInteractions(permanentCessationService);
    }

    @Test
    void execute_shouldThrowClassCastException_whenRequestIdNotString() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(123L);
       assertThrows(ClassCastException.class, () -> handler.execute(execution));
    }
}
