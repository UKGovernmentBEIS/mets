package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOperatorRespondService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class InstallationOnsiteInspectionOperatorRespondSubmitActionHandler
        implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final InstallationOnsiteInspectionOperatorRespondService installationOnsiteInspectionOperatorRespondService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update task payload
        installationOnsiteInspectionOperatorRespondService.applySubmitAction(requestTask, appUser);

        final InstallationInspectionOperatorRespondRequestTaskPayload taskPayload =
            (InstallationInspectionOperatorRespondRequestTaskPayload) requestTask.getPayload();

        workflowService.completeTask(requestTask.getProcessTaskId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SUBMIT);
    }
}
