package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditOperatorRespondService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondSaveRequestTaskActionPayload;

import java.util.List;

@RequiredArgsConstructor
@Component
public class InstallationAuditOperatorRespondSaveActionHandler
        implements RequestTaskActionHandler<InstallationInspectionOperatorRespondSaveRequestTaskActionPayload> {

    private final InstallationAuditOperatorRespondService installationAuditOperatorRespondService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        InstallationInspectionOperatorRespondSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        installationAuditOperatorRespondService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE);
    }
}