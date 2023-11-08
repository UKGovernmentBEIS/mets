package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewValidatorService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AerCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final AerReviewService aerReviewService;
    private final AerReviewValidatorService aerReviewValidatorService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        RequestTaskActionEmptyPayload actionPayload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate review groups decision
        AerApplicationReviewRequestTaskPayload taskPayload =
            (AerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        AerRequestPayload requestPayload = (AerRequestPayload) requestTask.getRequest().getPayload();

        aerReviewValidatorService.validateCompleted(taskPayload.getAer(), taskPayload.getReviewGroupDecisions(),
            requestPayload.isVerificationPerformed(), taskPayload.getVerificationReport());

        // Save data to request
        aerReviewService.updateRequestPayload(requestTask, pmrvUser);

        // Complete task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AER_REVIEW_OUTCOME, ReviewOutcome.COMPLETED)
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_COMPLETE_REVIEW);
    }
}
