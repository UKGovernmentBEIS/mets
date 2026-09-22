package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
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

    @Test
    void completeOpenVerifierSystemMessageNotificationRequests_completes_tasks_of_previous_verification_body() {

        Long accountId = 1L;
        Long previousVbId = 11L;
        Long appointedVbId = 21L;
        RequestTask previousVbTask = RequestTask.builder()
            .processTaskId("pt1")
            .type(RequestTaskType.NEW_VERIFICATION_BODY_EMITTER)
            .request(Request.builder().accountId(accountId).verificationBodyId(previousVbId).build())
            .build();
        RequestTask appointedVbTask = RequestTask.builder()
            .processTaskId("pt2")
            .type(RequestTaskType.NEW_VERIFICATION_BODY_EMITTER)
            .request(Request.builder().accountId(accountId).verificationBodyId(appointedVbId).build())
            .build();

        when(requestTaskRepository.findByTypeInAndRequestAccountIdIn(
            RequestTaskType.getVerifierSystemMessageNotificationTypes(), Set.of(accountId)))
            .thenReturn(List.of(previousVbTask, appointedVbTask));

        //invoke
        cut.completeOpenVerifierSystemMessageNotificationRequests(Set.of(accountId), appointedVbId);

        //verify
        verify(workflowService, times(1)).completeTask("pt1");
        verifyNoMoreInteractions(workflowService);
    }

    @Test
    void completeOpenVerifierSystemMessageNotificationRequests_when_unappointed() {

        Long accountId = 1L;
        RequestTask task = RequestTask.builder()
            .processTaskId("pt1")
            .type(RequestTaskType.NEW_VERIFICATION_BODY_EMITTER)
            .request(Request.builder().accountId(accountId).verificationBodyId(11L).build())
            .build();

        when(requestTaskRepository.findByTypeInAndRequestAccountIdIn(
            RequestTaskType.getVerifierSystemMessageNotificationTypes(), Set.of(accountId)))
            .thenReturn(List.of(task));

        //invoke
        cut.completeOpenVerifierSystemMessageNotificationRequests(Set.of(accountId), null);

        //verify
        verify(workflowService, times(1)).completeTask("pt1");
        verifyNoMoreInteractions(workflowService);
    }

}
