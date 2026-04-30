package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.returnofallowances;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service.ReturnOfAllowancesSubmittedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesSubmittedHandlerFlowableTest {

    @Mock
    private ReturnOfAllowancesSubmittedService service;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private ReturnOfAllowancesSubmittedHandlerFlowable handler;

    @Test
    void execute_callsServiceSubmit_withRequestIdFromExecutionVariable() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).submit(requestId);
        verifyNoMoreInteractions(service);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_callsServiceSubmit_withNull_whenRequestIdMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);

        handler.execute(execution);

        verify(service).submit(null);
        verifyNoMoreInteractions(service);
    }
}
