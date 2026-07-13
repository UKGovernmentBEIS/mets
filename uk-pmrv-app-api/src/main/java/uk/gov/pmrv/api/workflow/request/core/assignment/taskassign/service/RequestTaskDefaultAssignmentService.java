package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestTaskDefaultAssignmentService {

    private final List<UserRoleRequestTaskDefaultAssignmentService> userRoleRequestTaskDefaultAssignmentServices;

    /**
     * Assigns the provided request task to default assignee.
     * @param requestTask the {@link RequestTask}
     */
    @Transactional
    public void assignDefaultAssigneeToTask(String requestTaskRoleType, RequestTask requestTask) {
        getUserService(requestTaskRoleType).ifPresent(service -> service.assignDefaultAssigneeToTask(requestTask));
    }

    private Optional<UserRoleRequestTaskDefaultAssignmentService> getUserService(String requestTaskRoleType) {
        return userRoleRequestTaskDefaultAssignmentServices.stream()
            .filter(service -> service.getRoleType().equals(requestTaskRoleType))
            .findAny();
    }
}
