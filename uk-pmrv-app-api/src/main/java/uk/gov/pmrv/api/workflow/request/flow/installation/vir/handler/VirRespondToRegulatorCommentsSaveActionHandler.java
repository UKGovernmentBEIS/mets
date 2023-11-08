package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRespondToRegulatorCommentsService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class VirRespondToRegulatorCommentsSaveActionHandler implements RequestTaskActionHandler<VirSaveRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final VirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        VirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        virRespondToRegulatorCommentsService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
