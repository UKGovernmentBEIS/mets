package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.service.RequestAviationAerCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.validation.RequestAviationAerCorsiaReviewValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationAerCorsiaCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestAviationAerCorsiaReviewValidatorService aerCorsiaReviewValidatorService;
    private final RequestAviationAerCorsiaReviewService aerCorsiaReviewService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        RequestTaskActionEmptyPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // validate that all review groups have valid decisions
        AviationAerCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        AviationAerCorsiaRequestPayload aerRequestPayload = (AviationAerCorsiaRequestPayload) requestTask.getRequest().getPayload();

        aerCorsiaReviewValidatorService.validateAllReviewGroupsExistAndAccepted(reviewRequestTaskPayload,
            aerRequestPayload.isVerificationPerformed());

        // update request payload
        aerCorsiaReviewService.updateRequestPayloadWithReviewOutcome(requestTask, pmrvUser);

        // Complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.COMPLETED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_COMPLETE_REVIEW);
    }
}
