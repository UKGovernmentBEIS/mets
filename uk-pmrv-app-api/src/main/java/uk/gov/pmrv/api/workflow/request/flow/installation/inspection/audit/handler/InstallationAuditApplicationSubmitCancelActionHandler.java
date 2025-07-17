package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.InstallationInspectionSubmitOutcome;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Component
public class InstallationAuditApplicationSubmitCancelActionHandler
        implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {
    private final WorkflowService workflowService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME,
                        InstallationInspectionSubmitOutcome.CANCELLED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_AUDIT_CANCEL_APPLICATION);
    }
}
