package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCustomContext;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationRequestVerificationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.RequestAerSubmitService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AerRequestAmendsVerificationActionHandler implements RequestTaskActionHandler<AerApplicationRequestVerificationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAerSubmitService requestAerSubmitService;
    private final WorkflowService workflowService;


    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        AerApplicationRequestVerificationRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        requestAerSubmitService.sendAmendsToVerifier(payload, requestTask, appUser);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AER_OUTCOME, AerOutcome.VERIFICATION_REQUESTED,
                BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.AER_AMEND.getCode())
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION);
    }
}