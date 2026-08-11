package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.settings;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.settings.service.SettingsFeeService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplyScheduledFeeUpdatesHandlerFlowableTest {

    @InjectMocks
    private ApplyScheduledFeeUpdatesHandlerFlowable handler;

    @Mock
    private SettingsFeeService settingsFeeService;

    @Test
    void execute_callsService() {
        handler.execute(mock(DelegateExecution.class));

        verify(settingsFeeService).applyScheduledFeeUpdates();
    }

    @Test
    void execute_serviceThrows_doesNotPropagateException() {
        doThrow(new RuntimeException("DB error")).when(settingsFeeService).applyScheduledFeeUpdates();

        assertDoesNotThrow(() -> handler.execute(mock(DelegateExecution.class)));

        verify(settingsFeeService).applyScheduledFeeUpdates();
    }
}
