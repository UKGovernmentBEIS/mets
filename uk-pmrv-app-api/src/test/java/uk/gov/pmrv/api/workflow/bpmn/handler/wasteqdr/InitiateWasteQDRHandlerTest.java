package uk.gov.pmrv.api.workflow.bpmn.handler.wasteqdr;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRCreationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class InitiateWasteQDRHandlerTest {

    @InjectMocks
    private InitiateWasteQDRHandler initiateWasteQDRHandler;

    @Mock
    private RequestService requestService;

    @Mock
    private WasteQDRCreationService wasteQDRCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final Long accountId = 1L;

        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);

        // Invoke
        initiateWasteQDRHandler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(wasteQDRCreationService, times(1)).createWasteQDR(accountId);
    }
}
