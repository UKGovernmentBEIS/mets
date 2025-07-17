package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionSubmitNotifyOperatorActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;

import java.util.List;

@Component
public class InstallationOnsiteInspectionSubmitNotifyOperatorActionHandler extends InstallationInspectionSubmitNotifyOperatorActionHandler {
    public InstallationOnsiteInspectionSubmitNotifyOperatorActionHandler(RequestTaskService requestTaskService, List<InstallationInspectionSubmitService> installationInspectionSubmitServices, WorkflowService workflowService) {
        super(requestTaskService, installationInspectionSubmitServices, workflowService);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR);
    }

    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_SUBMIT_OUTCOME;
    }
}
