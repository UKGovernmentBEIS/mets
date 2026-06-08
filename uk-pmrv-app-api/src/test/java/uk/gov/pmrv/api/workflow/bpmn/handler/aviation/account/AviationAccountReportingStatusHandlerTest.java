package uk.gov.pmrv.api.workflow.bpmn.handler.aviation.account;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingStatusHandlerTest {

    @Mock
    private DelegateExecution delegateExecution;

    @Mock
    private AviationAccountReportingStatusService service;

    @InjectMocks
    AviationAccountReportingStatusHandler handler;

    @Test
    void execute_shouldInvokeService() throws Exception {
        handler.execute(delegateExecution);
        verify(service, times(1)).populateReportingStatusesForNewYear();
    }

}