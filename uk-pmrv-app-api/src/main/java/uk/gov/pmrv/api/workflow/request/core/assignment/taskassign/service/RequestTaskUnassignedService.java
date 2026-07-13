package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RequestTaskUnassignedService {

    private final List<RequestTaskUnassignedNotificationService> unassignedTaskNotificationServices;

    @Transactional
    public void unassignedTaskToNotify(String requestTaskRoleType, RequestTask requestTask) {
        getUnassignedNotificationService(requestTaskRoleType).ifPresent(service -> service.requestTaskUnassignedTaskNotification(requestTask));
    }

    private Optional<RequestTaskUnassignedNotificationService> getUnassignedNotificationService(String requestTaskRoleType) {
        return unassignedTaskNotificationServices.stream()
            .filter(service -> service.getRoleType().equals(requestTaskRoleType))
            .findAny();
    }
}
