package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service.AviationAerCorsiaCompleteService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaCompletedAddRequestActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaCompletedAddRequestActionHandler handler;

    @Mock
    private AviationAerCorsiaCompleteService aviationAerCorsiaCompleteService;

    @Test
    void execute() throws Exception {
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME)).thenReturn(ReviewOutcome.COMPLETED);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(aviationAerCorsiaCompleteService, times(1)).addRequestAction(requestId, false);
    }

}