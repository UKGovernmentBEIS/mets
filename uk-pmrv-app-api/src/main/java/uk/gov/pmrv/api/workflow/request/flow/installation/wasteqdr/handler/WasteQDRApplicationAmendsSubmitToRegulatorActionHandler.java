package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRAmendsSubmitService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WasteQDRApplicationAmendsSubmitToRegulatorActionHandler implements
        RequestTaskActionHandler<WasteQDRApplicationAmendsSubmitRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WasteQDRAmendsSubmitService amendsSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        WasteQDRApplicationAmendsSubmitRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        amendsSubmitService.submitToRegulator(payload, requestTask, appUser);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.WASTE_QDR_OUTCOME, WasteQDROutcome.SUBMITTED_TO_REGULATOR));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR);
    }
}
