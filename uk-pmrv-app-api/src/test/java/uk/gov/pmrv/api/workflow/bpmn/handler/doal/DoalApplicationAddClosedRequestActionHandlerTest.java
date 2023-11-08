package uk.gov.pmrv.api.workflow.bpmn.handler.doal;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.service.DoalCloseService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoalApplicationAddClosedRequestActionHandlerTest {

    @InjectMocks
    private DoalApplicationAddClosedRequestActionHandler handler;

    @Mock
    private DelegateExecution execution;

    @Mock
    private DoalCloseService doalCloseService;

    @Test
    void execute() {
        final String requestId = "1";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(doalCloseService, times(1)).addClosedRequestAction(requestId);
    }
}
