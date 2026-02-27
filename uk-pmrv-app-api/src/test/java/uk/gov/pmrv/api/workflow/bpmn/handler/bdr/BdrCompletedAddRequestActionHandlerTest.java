package uk.gov.pmrv.api.workflow.bpmn.handler.bdr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCompleteService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BdrCompletedAddRequestActionHandlerTest {


    @InjectMocks
    private BdrCompletedAddRequestActionHandler handler;

    @Mock
    private BDRCompleteService bdrCompleteService;

    @Mock
    private DelegateExecution execution;


    @Test
    void execute() throws Exception {

        String requestId = "BDR00001-2025";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(bdrCompleteService, times(1)).addRequestAction(requestId);
    }

}
