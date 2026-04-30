package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aer;

import org.flowable.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreateVirService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerInitiateVirHandlerFlowableTest {

    @InjectMocks
    private AerInitiateVirHandlerFlowable handler;

    @Mock
    private AerCreateVirService aerCreateVirService;

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
