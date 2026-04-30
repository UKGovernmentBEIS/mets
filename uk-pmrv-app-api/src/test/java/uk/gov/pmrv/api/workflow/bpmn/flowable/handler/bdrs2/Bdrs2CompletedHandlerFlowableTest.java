package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.bdrs2;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2CompleteService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Bdrs2CompletedHandlerFlowableTest {

    @InjectMocks
    private Bdrs2CompletedHandlerFlowable handler;

    @Mock
    private BDRS2CompleteService bdrs2CompleteService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String requestId = "BDRS2-00001-2025";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(bdrs2CompleteService, times(1)).complete(requestId);
    }
}
