package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionOperatorRespondSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionOperatorRespondService;

import java.util.List;


@RequiredArgsConstructor
@Component
public class InstallationOnsiteInspectionOperatorRespondSaveActionHandler
        implements RequestTaskActionHandler<InstallationInspectionOperatorRespondSaveRequestTaskActionPayload> {

    private final InstallationOnsiteInspectionOperatorRespondService installationOnsiteInspectionOperatorRespondService;
    private final RequestTaskService requestTaskService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        InstallationInspectionOperatorRespondSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        installationOnsiteInspectionOperatorRespondService.applySaveAction(requestTask, payload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE);
    }
}