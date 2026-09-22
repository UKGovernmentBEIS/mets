package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
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

    /**
     * Completes the open verifier system message notification tasks of the provided accounts, since the verifiers
     * of the previously appointed verification body are no longer allowed to access them.
     *
     * @param accountIds the accounts whose verification body appointment changed
     * @param appointedVerificationBodyId the newly appointed verification body, {@code null} when unappointed
     */
    public void completeOpenVerifierSystemMessageNotificationRequests(Set<Long> accountIds, Long appointedVerificationBodyId) {
        requestTaskRepository
            .findByTypeInAndRequestAccountIdIn(RequestTaskType.getVerifierSystemMessageNotificationTypes(), accountIds)
            .stream()
            .filter(rt -> !Objects.equals(appointedVerificationBodyId, rt.getRequest().getVerificationBodyId()))
            .forEach(rt -> workflowService.completeTask(rt.getProcessTaskId()));
    }

}
