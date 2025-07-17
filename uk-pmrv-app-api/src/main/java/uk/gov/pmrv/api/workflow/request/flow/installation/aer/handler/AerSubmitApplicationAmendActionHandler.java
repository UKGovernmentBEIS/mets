package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerSubmitService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerSubmitApplicationAmendActionHandler implements
    RequestTaskActionHandler<AerSubmitApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAerSubmitService requestAerSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        AerSubmitApplicationAmendRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestAerSubmitService.sendAmendsToRegulator(requestTask, payload, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AER_OUTCOME, AerOutcome.REVIEW_REQUESTED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND);
    }
}
