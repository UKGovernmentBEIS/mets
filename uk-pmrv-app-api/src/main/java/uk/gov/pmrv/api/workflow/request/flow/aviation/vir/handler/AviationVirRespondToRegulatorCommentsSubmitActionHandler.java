package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.handler;

import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service.AviationVirRespondToRegulatorCommentsService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@RequiredArgsConstructor
@Component
public class AviationVirRespondToRegulatorCommentsSubmitActionHandler 
    implements RequestTaskActionHandler<AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AviationVirRespondToRegulatorCommentsService virRespondToRegulatorCommentsService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser,
                        final AviationVirSubmitRespondToRegulatorCommentsRequestTaskActionPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update task payload
        virRespondToRegulatorCommentsService.applySubmitAction(payload, requestTask, pmrvUser);

        final AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload taskPayload =
                (AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload) requestTask.getPayload();

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
        return List.of(RequestTaskActionType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS);
    }
}
