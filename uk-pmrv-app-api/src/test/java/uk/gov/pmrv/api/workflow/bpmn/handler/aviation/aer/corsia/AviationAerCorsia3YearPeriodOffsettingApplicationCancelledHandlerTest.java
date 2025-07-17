package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.*;

public class AviationAerCorsia3YearPeriodOffsettingApplicationCancelledHandlerTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingApplicationCancelledHandler handler;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingSubmitService aviationAerCorsia3YearPeriodOffsettingSubmitService;

    @Mock
    private DelegateExecution execution;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_success() {
        String requestId = "12345";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        handler.execute(execution);
        verify(aviationAerCorsia3YearPeriodOffsettingSubmitService, times(1)).cancel(requestId);
    }

    @Test
    void testExecute_requestIdNotFound() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(aviationAerCorsia3YearPeriodOffsettingSubmitService, never()).cancel(anyString());
    }
}
