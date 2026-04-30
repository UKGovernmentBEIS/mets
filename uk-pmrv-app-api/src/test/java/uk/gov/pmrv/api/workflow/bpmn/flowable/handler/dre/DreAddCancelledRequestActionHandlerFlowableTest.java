package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreAddCancelledRequestActionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DreAddCancelledRequestActionHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private DreAddCancelledRequestActionService service;

    @InjectMocks
    private DreAddCancelledRequestActionHandlerFlowable handler;

    @Test
    void execute_callsServiceAdd_withRequestId() {
        String requestId = "REQ-1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);

        verify(service).add(requestId);
        verifyNoMoreInteractions(service);
        verify(execution).getVariable(BpmnProcessConstants.REQUEST_ID);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_callsServiceAdd_withNullRequestId_whenMissingVariable() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);

        handler.execute(execution);

        verify(service).add(null);
        verifyNoMoreInteractions(service);
    }
}
