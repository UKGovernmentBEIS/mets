package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskUnassignedService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestTaskCreateService {

    private final RequestService requestService;
    private final RequestTaskDefaultAssignmentService requestTaskDefaultAssignmentService;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    private final RequestTaskUnassignedService requestTaskUnassignedService;
    private final List<InitializeRequestTaskHandler> initializeRequestTaskHandlers;


    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType) {

        this.create(requestId, processTaskId, requestTaskType, null);
    }

    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType,
                       final String userToAssignTask) {

        this.create(requestId, processTaskId, requestTaskType, userToAssignTask, null);
    }
    
    public void create(final String requestId,
                       final String processTaskId,
                       final RequestTaskType requestTaskType,
                       final String userToAssignTask,
                       final LocalDate dueDate) {

        Request request = requestService.findRequestById(requestId);

        RequestTask requestTask =
            RequestTask.builder()
                .processTaskId(processTaskId)
                .type(requestTaskType)
                .payload(createRequestTaskPayload(requestTaskType, request))
                .startDate(LocalDateTime.now())
                .dueDate(dueDate)
                .build();
        
        request.addRequestTask(requestTask);

        String requestTaskRoleType = authorizationRulesQueryService
                .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name())
                .orElse(null);

        assignToUserOrAssignToDefaultUser(requestTaskRoleType, requestTask, userToAssignTask);

        if (requestTask.getAssignee() == null) {
            requestTaskUnassignedService.unassignedTaskToNotify(requestTaskRoleType, requestTask);
        }
    }

    private void assignToUserOrAssignToDefaultUser(String requestTaskRoleType, RequestTask requestTask, String userToAssignTask) {
        if(userToAssignTask != null) {
            requestTask.setAssignee(userToAssignTask);
        } else {
            requestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTaskRoleType, requestTask);
        }
    }

    private RequestTaskPayload createRequestTaskPayload(RequestTaskType requestTaskType, Request request) {
        Optional<InitializeRequestTaskHandler> initializer = initializeRequestTaskHandlers.stream()
            .filter(handler -> handler.getRequestTaskTypes().contains(requestTaskType))
            .findFirst();

        return initializer.map(handler -> handler.initializePayload(request))
            .orElse(null);
    }
}
