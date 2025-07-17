package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class AviationVirApplySaveActionHandler 
    implements RequestTaskActionHandler<AviationVirSaveApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirApplyService applyService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final AviationVirSaveApplicationRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        applyService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_VIR_SAVE_APPLICATION);
    }
}
