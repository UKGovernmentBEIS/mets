package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.empreissue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpReissueMarkAsCompletedHandlerFlowableTest {

    @InjectMocks
    private EmpReissueMarkAsCompletedHandlerFlowable cut;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        cut.execute(execution);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED, true);
    }
}
