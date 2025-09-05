package uk.gov.pmrv.api.workflow.request.flow.installation.alr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRAmendsSubmitService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ALRApplicationAmendsSubmitToRegulatorActionHandler implements
        RequestTaskActionHandler<ALRApplicationAmendsSubmitRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final ALRAmendsSubmitService amendsSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, ALRApplicationAmendsSubmitRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        amendsSubmitService.submitToRegulator(payload, requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.ALR_OUTCOME, ALROutcome.SUBMITTED_TO_REGULATOR));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.ALR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR);
    }
}
