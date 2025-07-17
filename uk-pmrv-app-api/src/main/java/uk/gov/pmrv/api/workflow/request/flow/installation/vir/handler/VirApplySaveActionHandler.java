package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.RequestVirApplyService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class VirApplySaveActionHandler implements RequestTaskActionHandler<VirSaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestVirApplyService requestVirApplyService;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        VirSaveApplicationRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestVirApplyService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.VIR_SAVE_APPLICATION);
    }
}
