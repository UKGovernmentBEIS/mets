package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDROutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRSubmitService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class WasteQDRApplicationSubmitToRegulatorRequestTaskActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final WasteQDRSubmitService wasteQDRSubmitService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        wasteQDRSubmitService.submitToRegulator(requestTask, appUser);

        requestTask.getRequest().setSubmissionDate(LocalDateTime.now());

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.WASTE_QDR_OUTCOME, WasteQDROutcome.SUBMITTED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WASTE_QDR_SUBMIT_TO_REGULATOR);
    }
}
