package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.aviation.account;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusPopulationService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusHandlerFlowableTest {

    @Mock
    private AviationAccountReportingStatusPopulationService aviationAccountReportingStatusPopulationService;

    @Mock
    private DelegateExecution delegateExecution;

    @InjectMocks
    private AviationAccountReportingStatusHandlerFlowable handler;

    @Test
    void execute_shouldPopulateReportingStatusesForNewYear() {
        handler.execute(delegateExecution);
        verify(aviationAccountReportingStatusPopulationService, times(1))
                .populateReportingStatusesForNewYear();
        verifyNoMoreInteractions(aviationAccountReportingStatusPopulationService);

        verifyNoInteractions(delegateExecution);
    }

    @Test
    void execute_shouldPropagateException_fromService() {
        RuntimeException ex = new RuntimeException("exception");
        doThrow(ex).when(aviationAccountReportingStatusPopulationService)
                .populateReportingStatusesForNewYear();
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> handler.execute(delegateExecution));

        verify(aviationAccountReportingStatusPopulationService, times(1))
                .populateReportingStatusesForNewYear();
        verifyNoMoreInteractions(aviationAccountReportingStatusPopulationService);
        verifyNoInteractions(delegateExecution);
    }
}
