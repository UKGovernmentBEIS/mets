package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.ExtendFollowUpExpirationTimerService;

import java.util.Date;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtendFollowUpExpirationTimerHandlerFlowableTest {

    @Mock
    DelegateExecution execution;

    @Mock
    ExtendFollowUpExpirationTimerService service;

    @InjectMocks
    ExtendFollowUpExpirationTimerHandlerFlowable handler;

    @Test
    void execute_callsServiceWithCorrectVariables() {
        Date date = new Date();

        when(execution.getVariable("requestId")).thenReturn("REQ-1");
        when(execution.getVariable("followUpResponseExpirationDate"))
                .thenReturn(date);

        handler.execute(execution);

        verify(service).extendTimer("REQ-1", date);
    }
}
