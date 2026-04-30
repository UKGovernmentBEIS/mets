package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.corsia;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AviationAerCorsia3YearPeriodOffsettingApplicationCancelledHandlerFlowableTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingApplicationCancelledHandlerFlowable handler;

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
