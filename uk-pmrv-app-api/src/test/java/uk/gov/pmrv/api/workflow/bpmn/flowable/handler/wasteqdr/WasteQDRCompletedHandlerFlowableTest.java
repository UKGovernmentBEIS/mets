package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.wasteqdr;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WasteQDRCompletedHandlerFlowableTest {

    @Mock
    private DelegateExecution delegateExecution;

    @InjectMocks
    private WasteQDRCompletedHandlerFlowable handler;

    @Test
    void execute_doesNothing() {
        assertDoesNotThrow(() -> handler.execute(delegateExecution));
        verifyNoInteractions(delegateExecution);
    }
}
