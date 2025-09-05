package uk.gov.pmrv.api.workflow.bpmn.handler.hseti;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETICompleteService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HsetiCompletedAddWithdrawnRequestActionHandlerTest {

    @InjectMocks
    private HsetiCompletedAddWithdrawnRequestActionHandler handler;

    @Mock
    private HSETICompleteService completeService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {

        String requestId = "HSETI00177-2021_2025";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(completeService, times(1)).addWithdrawnRequestAction(requestId);
    }
}
