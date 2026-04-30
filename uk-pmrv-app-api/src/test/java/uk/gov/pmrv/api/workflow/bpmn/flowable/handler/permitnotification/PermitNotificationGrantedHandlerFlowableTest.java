package uk.gov.pmrv.api.workflow.bpmn.flowable.handler.permitnotification;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationReviewSubmittedService;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGrantedHandlerFlowableTest {

    @Mock
    private DelegateExecution execution;

    @Mock
    private PermitNotificationReviewSubmittedService reviewSubmittedService;

    @InjectMocks
    private PermitNotificationGrantedHandlerFlowable handler;

    @Test
    void execute_runsGrantedPostActions_andSetsFollowUpNeededVariable() {
        String requestId = "REQ-123";
        boolean followUpNeeded = true;

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(reviewSubmittedService.isFollowUpNeeded(requestId)).thenReturn(followUpNeeded);

        handler.execute(execution);
        InOrder inOrder = inOrder(reviewSubmittedService, execution);

        inOrder.verify(reviewSubmittedService).executeGrantedPostActions(requestId);
        inOrder.verify(reviewSubmittedService).isFollowUpNeeded(requestId);
        inOrder.verify(execution).setVariable(BpmnProcessConstants.FOLLOW_UP_RESPONSE_NEEDED, followUpNeeded);

        verifyNoMoreInteractions(reviewSubmittedService);
    }
}
