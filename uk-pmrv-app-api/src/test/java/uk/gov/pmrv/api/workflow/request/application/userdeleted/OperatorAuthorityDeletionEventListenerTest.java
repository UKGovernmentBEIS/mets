package uk.gov.pmrv.api.workflow.request.application.userdeleted;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.operator.event.OperatorAuthorityDeletionEvent;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service.SystemMessageNotificationRequestService;

@ExtendWith(MockitoExtension.class)
class OperatorAuthorityDeletionEventListenerTest {

    @InjectMocks
    private OperatorAuthorityDeletionEventListener operatorAuthorityDeletionEventListener;

    @Mock
    private SystemMessageNotificationRequestService systemMessageNotificationRequestService;

    @Mock
    private OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Test
    void onOperatorUserDeletionEvent() throws BusinessCheckedException {
        String userId = "userId";
        Long accountId = 1L;
        OperatorAuthorityDeletionEvent deletionEvent = OperatorAuthorityDeletionEvent.builder()
            .accountId(accountId)
            .userId(userId)
            .build();

        operatorAuthorityDeletionEventListener.onOperatorUserDeletionEvent(deletionEvent);

        verify(systemMessageNotificationRequestService, times(1))
            .completeOpenSystemMessageNotificationRequests(userId, accountId);
        verify(operatorRequestTaskAssignmentService, times(1))
            .assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);
    }
}