package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.doe.corsia;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service.AviationDoECorsiaCancelAerService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDoECorsiaCancelAerHandlerFlowableTest {
    @InjectMocks
    private AviationDoECorsiaCancelAerHandlerFlowable handler;

    @Mock
    private AviationDoECorsiaCancelAerService cancelAerService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        // given
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // when
        handler.execute(execution);

        // then
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(cancelAerService, times(1)).process(requestId);
    }
}
