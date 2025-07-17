package uk.gov.pmrv.api.workflow.request.application.accountcontactassigned;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.operator.OperatorRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FirstPrimaryContactAssignedToAccountEventService {

    private final RequestTaskRepository requestTaskRepository;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    private final OperatorRequestTaskAssignmentService operatorRequestTaskAssignmentService;

    @Transactional
    public void assignUnassignedTasksToAccountPrimaryContact(Long accountId, String userId) {
        //find all unassigned tasks for the account id
        List<RequestTask> unassignedRequestTasks = requestTaskRepository
            .findByAssigneeAndRequestAccountId(null, accountId);

        if(!unassignedRequestTasks.isEmpty()) {
            //filter unassigned tasks using the user role (OPERATOR)
            Set<String> operatorRequestTaskTypes = authorizationRulesQueryService
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleTypeConstants.OPERATOR);

            List<RequestTask> unassignedOperatorRelatedRequestTasks = unassignedRequestTasks.stream()
                .filter(requestTask -> operatorRequestTaskTypes.contains(requestTask.getType().name()))
                .toList();

            //assign tasks to user
            unassignedOperatorRelatedRequestTasks.forEach(
                requestTask -> operatorRequestTaskAssignmentService.assignTask(requestTask, userId));
        }
    }
}
