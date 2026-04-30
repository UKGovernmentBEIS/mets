package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.alr;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAuthorityResponseService;

@ExtendWith(MockitoExtension.class)
class AlrApplicationAddAcceptedRequestActionHandlerFlowableTest {

    @Mock
    private ALRAuthorityResponseService alrAuthorityResponseService;

    @Mock
    private DelegateExecution execution;

    @InjectMocks
    private AlrApplicationAddAcceptedRequestActionHandlerFlowable handler;

    @Test
    void execute_shouldAddAcceptedRequestAction() {
        String requestId = "REQ-12345";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        verify(alrAuthorityResponseService)
                .addSubmittedRequestAction(requestId, RequestActionType.ALR_APPLICATION_ACCEPTED);
    }

    @Test
    void execute_shouldPassNullRequestId_whenExecutionVariableMissing() {
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(null);
        handler.execute(execution);
        verify(alrAuthorityResponseService)
                .addSubmittedRequestAction(null, RequestActionType.ALR_APPLICATION_ACCEPTED);
    }
}
