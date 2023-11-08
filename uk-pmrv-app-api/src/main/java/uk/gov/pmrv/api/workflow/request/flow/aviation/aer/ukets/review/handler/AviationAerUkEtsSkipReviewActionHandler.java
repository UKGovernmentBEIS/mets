package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
class AviationAerUkEtsSkipReviewActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerUkEtsReviewService aerUkEtsReviewService;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final PmrvUser pmrvUser,
                        final RequestTaskActionEmptyPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // update request payload
        aerUkEtsReviewService.updateRequestPayloadWithSkipReviewOutcome(requestTask, pmrvUser);

        // Complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                   BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.SKIPPED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_UKETS_SKIP_REVIEW);
    }
}
