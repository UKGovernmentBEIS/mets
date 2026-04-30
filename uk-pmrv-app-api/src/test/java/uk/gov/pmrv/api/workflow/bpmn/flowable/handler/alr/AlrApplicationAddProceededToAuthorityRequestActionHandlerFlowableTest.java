package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRSubmitService;

@ExtendWith(MockitoExtension.class)
class AlrApplicationAddProceededToAuthorityRequestActionHandlerFlowableTest {

    @Mock
    private ALRSubmitService alrSubmitService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private AlrApplicationAddProceededToAuthorityRequestActionHandlerFlowable handler;

    @Test
    void execute_shouldAddProceededToAuthorityRequestAction() {
        String requestId = "REQ-12345";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        verify(alrSubmitService).addProceededToAuthorityRequestAction(requestId);
    }

    @Test
    void execute_shouldPassNullRequestId_whenExecutionVariableMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(alrSubmitService).addProceededToAuthorityRequestAction(null);
    }
}
