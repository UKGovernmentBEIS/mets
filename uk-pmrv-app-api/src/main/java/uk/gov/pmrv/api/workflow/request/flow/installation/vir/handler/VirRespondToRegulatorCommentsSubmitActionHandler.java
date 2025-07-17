package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.service.VirRespondToRegulatorCommentsService;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Component
public class VirRespondToRegulatorCommentsSubmitActionHandler implements RequestTaskActionHandler<VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final VirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update task payload
        virRespondToRegulatorCommentsService.applySubmitAction(payload, requestTask, appUser);

        final VirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (VirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();

        if(taskPayload.getRegulatorImprovementResponses().isEmpty()) {
            // Complete task
            workflowService.completeTask(requestTask.getProcessTaskId());
        }
        else{
            // Send message event to trigger due date change
            workflowService.sendEvent(requestTask.getRequest().getId(), BpmnProcessConstants.VIR_RESPONSE_COMMENT_SUBMITTED, new HashMap<>());
        }
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
