package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRespondToRegulatorCommentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@RequiredArgsConstructor
@Component
public class AviationVirRespondToRegulatorCommentsSaveActionHandler 
    implements RequestTaskActionHandler<AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final AviationVirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        virRespondToRegulatorCommentsService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
