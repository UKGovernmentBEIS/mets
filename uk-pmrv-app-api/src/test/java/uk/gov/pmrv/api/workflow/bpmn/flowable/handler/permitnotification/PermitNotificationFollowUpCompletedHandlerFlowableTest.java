package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationFollowUpCompletedHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;

    @InjectMocks
    private PermitNotificationFollowUpCompletedHandlerFlowable handler;

    @Test
    void execute_callsFollowUpCompletedPostActionsWithRequestId() {
        String requestId = "REQ-123";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        verify(reviewSubmittedService).executeFollowUpCompletedPostActions(requestId);
        verifyNoMoreInteractions(reviewSubmittedService);
    }
}
