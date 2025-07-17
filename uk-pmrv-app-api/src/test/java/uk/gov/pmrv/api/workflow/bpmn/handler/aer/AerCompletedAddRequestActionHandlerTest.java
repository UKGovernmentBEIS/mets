package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCompleteService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCompletedAddRequestActionHandlerTest {

    @InjectMocks
    private AerCompletedAddRequestActionHandler handler;

    @Mock
    private AerCompleteService aerCompleteService;

    @Test
    void execute_not_skipped() throws Exception {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AER_REVIEW_OUTCOME)).thenReturn(ReviewOutcome.COMPLETED);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(aerCompleteService, times(1)).addRequestAction(requestId, false);
    }

    @Test
    void execute_skipped() throws Exception {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AER_REVIEW_OUTCOME)).thenReturn(ReviewOutcome.SKIPPED);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(aerCompleteService, times(1)).addRequestAction(requestId, true);
    }
}
