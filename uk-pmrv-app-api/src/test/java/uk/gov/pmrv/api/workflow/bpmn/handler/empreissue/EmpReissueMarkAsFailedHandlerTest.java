package uk.gov.pmrv.api.workflow.bpmn.handler.empreissue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class EmpReissueMarkAsFailedHandlerTest {

	@InjectMocks
    private EmpReissueMarkAsFailedHandler cut;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        cut.execute(execution);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REISSUE_REQUEST_SUCCEEDED, false);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
    
}
