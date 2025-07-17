package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestTaskDefaultAssignmentService {

    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    private final List<UserRoleRequestTaskDefaultAssignmentService> userRoleRequestTaskDefaultAssignmentServices;

    /**
     * Assigns the provided request task to default assignee.
     * @param requestTask the {@link RequestTask}
     */
    @Transactional
    public void assignDefaultAssigneeToTask(RequestTask requestTask) {
        getUserService(requestTask).ifPresent(service -> service.assignDefaultAssigneeToTask(requestTask));
    }

    private Optional<UserRoleRequestTaskDefaultAssignmentService> getUserService(RequestTask requestTask) {
    	String requestTaskRoleType = authorizationRulesQueryService
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name())
            .orElse(null);

        return userRoleRequestTaskDefaultAssignmentServices.stream()
            .filter(service -> service.getRoleType().equals(requestTaskRoleType))
            .findAny();
    }
}
