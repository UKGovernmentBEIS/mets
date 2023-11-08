package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.aer.corsia;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreateVirService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaInitiateVirHandlerTest {

    @InjectMocks
    private AviationAerCorsiaInitiateVirHandler handler;

    @Mock
    private AviationAerCreateVirService aerCreateVirService;

    @Test
    void execute() throws Exception {
        
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(aerCreateVirService, times(1)).createRequestVir(requestId);
    }
}
