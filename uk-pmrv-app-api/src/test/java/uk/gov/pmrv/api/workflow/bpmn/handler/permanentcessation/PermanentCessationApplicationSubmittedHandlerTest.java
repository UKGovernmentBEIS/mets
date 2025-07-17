package uk.gov.pmrv.api.workflow.bpmn.handler.permanentcessation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.service.PermanentCessationSubmittedService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermanentCessationApplicationSubmittedHandlerTest {

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private PermanentCessationSubmittedService permanentCessationSubmittedService;

    @InjectMocks
    private PermanentCessationApplicationSubmittedHandler handler;

    private static final String REQUEST_ID = "test-request-id";

    @BeforeEach
    void setUp() {
        when(delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(REQUEST_ID);
    }

    @Test
    void execute_shouldInvokeServiceWithRequestId() throws Exception {
        handler.execute(delegateExecution);
        verify(permanentCessationSubmittedService, times(1)).submit(REQUEST_ID);
    }
}