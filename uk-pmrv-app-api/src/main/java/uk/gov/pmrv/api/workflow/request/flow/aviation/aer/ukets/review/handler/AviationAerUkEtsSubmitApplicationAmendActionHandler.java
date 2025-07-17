package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AviationAerUkEtsSubmitApplicationAmendActionHandler
    implements RequestTaskActionHandler<AviationAerSubmitApplicationAmendRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerUkEtsReviewService requestAviationAerUkEtsReviewService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        AviationAerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        requestAviationAerUkEtsReviewService.sendAmendedAerToRegulator(taskActionPayload, requestTask, appUser);

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_OUTCOME, AviationAerOutcome.REVIEW_REQUESTED));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND);
    }
}
