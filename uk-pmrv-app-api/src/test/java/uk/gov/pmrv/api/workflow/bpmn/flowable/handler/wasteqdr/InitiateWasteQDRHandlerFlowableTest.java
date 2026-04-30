package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRCreationService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiateWasteQDRHandlerFlowableTest {

    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private WasteQDRCreationService wasteQDRCreationService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private InitiateWasteQDRHandlerFlowable handler;

    @Test
    void execute_shouldCreateWasteQdr() {
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(ACCOUNT_ID);

        handler.execute(execution);

        verify(execution).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(wasteQDRCreationService).createWasteQDR(ACCOUNT_ID);
        verifyNoMoreInteractions(wasteQDRCreationService);
    }

    @Test
    void execute_whenServiceThrowsException_shouldNotPropagate() {
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(ACCOUNT_ID);
        doThrow(new RuntimeException("error")).when(wasteQDRCreationService).createWasteQDR(ACCOUNT_ID);

        assertDoesNotThrow(() -> handler.execute(execution));

        verify(execution).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(wasteQDRCreationService).createWasteQDR(ACCOUNT_ID);
        verifyNoMoreInteractions(wasteQDRCreationService);
    }
}
