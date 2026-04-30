package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCompleteService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BdrCompletedHandlerFlowableTest {

    @Mock
    private BDRCompleteService bdrCompleteService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private BdrCompletedHandlerFlowable handler;

    @Test
    void execute_callsCompleteWithRequestId() {
        String requestId = "REQ-001";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(bdrCompleteService).complete(requestId);
        verifyNoMoreInteractions(bdrCompleteService);
    }
}
