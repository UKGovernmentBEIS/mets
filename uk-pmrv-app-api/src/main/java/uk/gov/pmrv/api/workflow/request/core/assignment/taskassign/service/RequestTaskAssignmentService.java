package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.pmrv.api.workflow.request.core.assignment.requestassign.RequestAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.common.EmailNotificationAssignedTaskService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestTaskAssignmentService {

    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;
    private final RequestAssignmentService requestAssignmentService;
    private final RequestTaskService requestTaskService;
    private final UserRoleTypeService userRoleTypeService;
    private final EmailNotificationAssignedTaskService emailNotificationAssignedTaskService;

    /**
     * Assigns the {@code requestTask} to the provided {@code userId} after checking {@code userId}
     * capability on task.
     * @param requestTask the {@link RequestTask}
     * @param userId the user id
     * @throws BusinessCheckedException when user is not eligible to be assigned to task
     */
    public void assignToUser(RequestTask requestTask, String userId, String role) throws BusinessCheckedException {
        if(!requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, userId)){
            log.error("User '{}' has not the appropriate permission to be assigned to task '{}'",
                () -> userId, requestTask::getId);
            throw new BusinessCheckedException("User is not eligible to be assigned to task");
        }
        //assign task to user
        doAssignTaskToUser(requestTask, userId);

        //assign all other non cascadeable request tasks to user as well
        if(requestTask.getType().doesCascadeReassignment()) {
        	assignAllOtherCascadeableRequestTasksToUser(requestTask, userId);
        }
        
        // assign request payload
        if(requestTask.getType().doesPopulateRequestAssignment()) {
            requestAssignmentService.assignRequestToUser(requestTask.getRequest(), userId);
        }

        //notify user by email
        boolean sendEmailNotification = Optional.ofNullable(requestTask.getPayload()).map(RequestTaskPayload::isSendEmailNotification).orElse(true);
        if (sendEmailNotification) {
            emailNotificationAssignedTaskService.sendEmailToRecipient(userId, requestTask, role);
        }
    }

    private void assignAllOtherCascadeableRequestTasksToUser(RequestTask requestTask, String userId) {
        UserRoleTypeDTO userRoleType = userRoleTypeService.getUserRoleTypeByUserId(userId);
        List<RequestTask> requestTasksToBeAssigned = getAllOtherCascadeableRequestTasksByUserRoleType(requestTask, userRoleType.getRoleType());
        requestTasksToBeAssigned.forEach(
            taskToBeAssigned -> {
                if(!userId.equals(taskToBeAssigned.getAssignee()) &&
                    requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(taskToBeAssigned, userId)) {
                    doAssignTaskToUser(taskToBeAssigned, userId);
                }
            });
    }

    private List<RequestTask> getAllOtherCascadeableRequestTasksByUserRoleType(RequestTask task, String roleType) {
        List<RequestTask> requestTasks = requestTaskService.findTasksByRequestIdAndRoleType(task.getRequest().getId(), roleType);
        requestTasks.remove(task);

        return requestTasks.stream()
            .filter(requestTask -> requestTask.getType().doesCascadeReassignment())
            .collect(Collectors.toList());
    }

    private void doAssignTaskToUser(RequestTask requestTask, String userId) {
        requestTask.setAssignee(userId);
    }
}
