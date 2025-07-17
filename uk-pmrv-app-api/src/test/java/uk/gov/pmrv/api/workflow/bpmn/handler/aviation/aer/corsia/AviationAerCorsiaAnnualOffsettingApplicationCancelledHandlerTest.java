package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.*;

class AviationAerCorsiaAnnualOffsettingApplicationCancelledHandlerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingApplicationCancelledHandler handler;

    @Mock
    private AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;

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
        verify(aviationAerCorsiaAnnualOffsettingSubmitService, times(1)).cancel(requestId);
    }

    @Test
    void testExecute_requestIdNotFound() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(aviationAerCorsiaAnnualOffsettingSubmitService, never()).cancel(anyString());
    }
}
