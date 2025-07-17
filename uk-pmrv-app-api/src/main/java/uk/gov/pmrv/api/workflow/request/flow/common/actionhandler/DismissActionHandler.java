package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

/**
 * DismissActionHandler for closing request.
 */
@Component
@RequiredArgsConstructor
public class DismissActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(
            RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE,
            RequestTaskActionType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE,
            RequestTaskActionType.SYSTEM_MESSAGE_DISMISS
        );
    }
}