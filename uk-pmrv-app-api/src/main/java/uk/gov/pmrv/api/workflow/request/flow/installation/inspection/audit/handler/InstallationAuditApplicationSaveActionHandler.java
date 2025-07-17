package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service.InstallationAuditSubmitService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class InstallationAuditApplicationSaveActionHandler
        implements RequestTaskActionHandler<InstallationAuditApplicationSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final InstallationAuditSubmitService installationAuditSubmitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        InstallationAuditApplicationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        installationAuditSubmitService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_AUDIT_SAVE_APPLICATION);
    }
}
