package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

@ExtendWith(MockitoExtension.class)
class SystemMessageNotificationRequestServiceTest {

    @InjectMocks
    private SystemMessageNotificationRequestService cut;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private WorkflowService workflowService;

    @Test
    void completeOpenSystemMessageNotificationRequests_by_assignee() {

        String assignee = "assignee";
        List<RequestTask> notificationRequestTasks = List.of(RequestTask.builder().processTaskId("pt1").build());

        when(requestTaskRepository
            .findByRequestTypeAndAssignee(RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee))
            .thenReturn(notificationRequestTasks);

        //invoke
        cut.completeOpenSystemMessageNotificationRequests(assignee);

        //verify
        verify(workflowService, times(1)).completeTask("pt1");
    }

    @Test
    void completeOpenSystemMessageNotificationRequests_by_assignee_and_account() {

        String assignee = "assignee";
        Long accountId = 1L;
        List<RequestTask> notificationRequestTasks = List.of(RequestTask.builder().processTaskId("pt1").build());

        when(requestTaskRepository
            .findByRequestTypeAndAssigneeAndRequestAccountId(RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee, accountId))
            .thenReturn(notificationRequestTasks);

        //invoke
        cut.completeOpenSystemMessageNotificationRequests(assignee, accountId);

        //verify
        verify(workflowService, times(1)).completeTask("pt1");
    }

}
