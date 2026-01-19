package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRCompleteService;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class WasteQDRCompletedAddRequestActionHandlerTest {


    @InjectMocks
    private WasteQDRCompletedAddRequestActionHandler handler;

    @Mock
    private WasteQDRCompleteService bdrCompleteService;

    @Mock
    private DelegateExecution execution;


    @Test
    void execute() throws Exception {

        String requestId = "WQDR00001-2025-Q3";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(bdrCompleteService, times(1)).addRequestAction(requestId);
    }

}
