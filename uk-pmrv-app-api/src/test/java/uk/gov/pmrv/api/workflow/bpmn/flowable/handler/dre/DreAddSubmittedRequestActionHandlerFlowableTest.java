package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.dre;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreAddSubmittedRequestActionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DreAddSubmittedRequestActionHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private DreAddSubmittedRequestActionService service;

    @InjectMocks
    private DreAddSubmittedRequestActionHandlerFlowable handler;

    @Test
    void execute_callsServiceAdd_withRequestId() {
        String requestId = "REQ-2";
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
