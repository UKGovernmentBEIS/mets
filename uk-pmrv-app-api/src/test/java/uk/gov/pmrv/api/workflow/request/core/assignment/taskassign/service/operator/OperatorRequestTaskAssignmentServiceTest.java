package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorRequestTaskAssignmentServiceTest {

    @InjectMocks
    private OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Mock
    private RequestTaskReleaseService requestTaskReleaseService;

    @Test
    void assignUserTasksToAccountPrimaryContactOrRelease() throws BusinessCheckedException {
        Long accountId = 1L;
        String userId = "userId";
        String primaryContact = "primaryContact";

        RequestTask requestTask = RequestTask.builder().assignee(userId).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestAccountId(userId, accountId))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService.findPrimaryContactByAccount(accountId)).thenReturn(Optional.of(primaryContact));

        operatorRequestTaskAssignmentService.assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);

        verify(requestTaskRepository, times(1))
            .findByAssigneeAndRequestAccountId(userId, accountId);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(accountId);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verifyNoInteractions(requestTaskReleaseService);
    }

    @Test
    void assignUserTasksToAccountPrimaryContactOrRelease_task_can_not_be_re_assigned() throws BusinessCheckedException {
        Long accountId = 1L;
        String userId = "userId";
        String primaryContact = "primaryContact";

        RequestTask requestTask = RequestTask.builder().assignee(userId).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestAccountId(userId, accountId))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService.findPrimaryContactByAccount(accountId)).thenReturn(Optional.of(primaryContact));
        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, primaryContact);

        operatorRequestTaskAssignmentService.assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);

        verify(requestTaskRepository, times(1))
            .findByAssigneeAndRequestAccountId(userId, accountId);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(accountId);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, primaryContact);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
    }

    @Test
    void assignUserTasksToAccountPrimaryContactOrRelease_no_primary_contact_found() {
        Long accountId = 1L;
        String userId = "userId";

        RequestTask requestTask = RequestTask.builder().assignee(userId).build();

        when(requestTaskRepository
            .findByAssigneeAndRequestAccountId(userId, accountId))
            .thenReturn(List.of(requestTask));
        when(accountContactQueryService.findPrimaryContactByAccount(accountId)).thenReturn(Optional.empty());

        operatorRequestTaskAssignmentService.assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);

        verify(requestTaskRepository, times(1))
            .findByAssigneeAndRequestAccountId(userId, accountId);
        verify(accountContactQueryService, times(1)).findPrimaryContactByAccount(accountId);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
        verifyNoInteractions(requestTaskAssignmentService);
    }

    @Test
    void assignUserTasksToAccountPrimaryContactOrRelease_no_tasks_found_for_user() {
        Long accountId = 1L;
        String userId = "userId";

        when(requestTaskRepository
            .findByAssigneeAndRequestAccountId(userId, accountId))
            .thenReturn(Collections.emptyList());

        operatorRequestTaskAssignmentService.assignUserTasksToAccountPrimaryContactOrRelease(userId, accountId);

        verify(requestTaskRepository, times(1))
            .findByAssigneeAndRequestAccountId(userId, accountId);
    }

    @Test
    void assignTask() throws BusinessCheckedException {
        String userId = "userId";

        Request request = Request.builder().status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        operatorRequestTaskAssignmentService.assignTask(requestTask, userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
    }

    @Test
    void assignTask_throws_exception() throws BusinessCheckedException {
        String userId = "userId";

        Request request = Request.builder().status(RequestStatus.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).build();

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, userId);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> operatorRequestTaskAssignmentService.assignTask(requestTask, userId));

        assertEquals(ErrorCode.ASSIGNMENT_NOT_ALLOWED, businessException.getErrorCode());

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, userId);
        verifyNoMoreInteractions(requestTaskAssignmentService);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleTypeConstants.OPERATOR, operatorRequestTaskAssignmentService.getRoleType());
    }

}