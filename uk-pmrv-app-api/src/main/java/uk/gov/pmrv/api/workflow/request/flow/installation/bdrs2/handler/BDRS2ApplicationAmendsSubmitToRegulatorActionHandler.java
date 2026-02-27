package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Outcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2AmendsSubmitService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BDRS2ApplicationAmendsSubmitToRegulatorActionHandler implements
        RequestTaskActionHandler<BDRS2ApplicationAmendsSubmitRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final BDRS2AmendsSubmitService amendsSubmitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        BDRS2ApplicationAmendsSubmitRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        amendsSubmitService.submitToRegulator(payload, requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.BDRS2_OUTCOME, BDRS2Outcome.SUBMITTED_TO_REGULATOR));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.BDRS2_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR);
    }
}
