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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRegulatorReviewSubmitService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WasteQDRRegulatorReviewSubmitActionHandler
        implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final WasteQDRRegulatorReviewSubmitService submitService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        submitService.submit(requestTask, appUser);

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, ((WasteQDRRequestPayload) requestTask.getRequest().getPayload()).getReviewDecision().getType(),
                BpmnProcessConstants.WASTE_QDR_REGULATOR_REVIEW_OUTCOME, ReviewOutcome.COMPLETED)
        );
    }


    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.WASTE_QDR_REGULATOR_REVIEW_SUBMIT);
    }
}
