package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferASaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferAApplyService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PermitTransferASaveApplicationActionHandler
    implements RequestTaskActionHandler<PermitTransferASaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitTransferAApplyService applyService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitTransferASaveApplicationRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        applyService.applySaveAction(requestTask, actionPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_TRANSFER_A_SAVE_APPLICATION);
    }
}
