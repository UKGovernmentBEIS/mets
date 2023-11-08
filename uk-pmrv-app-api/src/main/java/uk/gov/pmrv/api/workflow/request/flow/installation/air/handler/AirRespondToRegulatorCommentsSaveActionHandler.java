package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSaveRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirRespondToRegulatorCommentsService;

@RequiredArgsConstructor
@Component
public class AirRespondToRegulatorCommentsSaveActionHandler
    implements RequestTaskActionHandler<AirSaveRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AirRespondToRegulatorCommentsService respondToRegulatorCommentsService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final AirSaveRespondToRegulatorCommentsRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        respondToRegulatorCommentsService.applySaveAction(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
