package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRAmendsSubmitService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class BDRApplicationAmendsSubmitToVerifierActionHandler
        implements RequestTaskActionHandler<BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload> {


    private final RequestTaskService requestTaskService;
    private final BDRAmendsSubmitService amendsSubmitService;
    private final WorkflowService workflowService;


    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        BDRApplicationAmendsSubmitToVerifierRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        amendsSubmitService.sendAmendsToVerifier(payload, requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.BDR_OUTCOME, BDROutcome.SUBMITTED_TO_VERIFIER,
                BpmnProcessConstants.REQUEST_TYPE_DYNAMIC_TASK_PREFIX, RequestCustomContext.BDR_AMEND.getCode())
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER);
    }
}
