package uk.gov.pmrv.api.workflow.request.flow.installation.air.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.service.AirRespondToRegulatorCommentsService;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AirRespondToRegulatorCommentsSubmitActionHandler
    implements RequestTaskActionHandler<AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AirRespondToRegulatorCommentsService respondToRegulatorCommentsService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update task payload
        respondToRegulatorCommentsService.applySubmitAction(payload, requestTask, appUser);

        final AirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
            (AirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();

        if (taskPayload.getRegulatorImprovementResponses().size() == taskPayload.getRespondedItems().size()) {
            // Complete task
            workflowService.completeTask(requestTask.getProcessTaskId());
        } else {
            // Send message event to trigger due date change
            workflowService.sendEvent(
                requestTask.getRequest().getId(),
                BpmnProcessConstants.AIR_RESPONSE_COMMENT_SUBMITTED, 
                new HashMap<>()
            );
        }
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
