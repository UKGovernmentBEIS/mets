package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;


@RequiredArgsConstructor
@Service
public class SystemMessageNotificationRequestService {

    private final RequestTaskRepository requestTaskRepository;
    private final WorkflowService workflowService;

    public void completeOpenSystemMessageNotificationRequests(String assignee) {
        requestTaskRepository
            .findByRequestTypeAndAssignee(RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee)
            .forEach(rt -> workflowService.completeTask(rt.getProcessTaskId()));
    }

    public void completeOpenSystemMessageNotificationRequests(String assignee, Long accountId) {
        requestTaskRepository.findByRequestTypeAndAssigneeAndRequestAccountId(
            RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee, accountId)
            .forEach(rt -> workflowService.completeTask(rt.getProcessTaskId()));
    }


}
