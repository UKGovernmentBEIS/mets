package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.aer.ukets;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreateVirService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsInitiateVirHandlerFlowableTest {

    @InjectMocks
    private AviationAerUkEtsInitiateVirHandlerFlowable handler;

    @Mock
    private AviationAerCreateVirService aerCreateVirService;

    @Test
    void execute() {
        
        final DelegateExecution execution = mock(DelegateExecution.class);
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(aerCreateVirService, times(1)).createRequestVir(requestId);
    }
}
