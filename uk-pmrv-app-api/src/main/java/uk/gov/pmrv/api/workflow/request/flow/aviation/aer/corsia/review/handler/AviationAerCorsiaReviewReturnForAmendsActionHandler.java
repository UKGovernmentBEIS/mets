package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.domain.AviationAerCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.review.mapper.AviationAerCorsiaReviewMapper;
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
public class AviationAerCorsiaReviewReturnForAmendsActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload>  {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final RequestAviationAerCorsiaReviewValidatorService aerCorsiaReviewValidatorService;
    private final RequestAviationAerCorsiaReviewService aerCorsiaReviewService;
    private final WorkflowService workflowService;

    private final AviationAerCorsiaReviewMapper aviationAerCorsiaReviewMapper;
    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, RequestTaskActionEmptyPayload payload) {

        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate that at least one review group is 'Operator to amend'
        aerCorsiaReviewValidatorService.validateAtLeastOneReviewGroupAmendsNeeded(requestTaskPayload);

        // Update request payload
        aerCorsiaReviewService.updateRequestPayloadWithReviewOutcome(requestTask, pmrvUser);

        // Add request action
        createRequestAction(requestTask, pmrvUser);

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED));

    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AviationAerCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = (AviationAerCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();

        AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload requestActionPayload =
                aviationAerCorsiaReviewMapper.toAviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload(
                        requestTaskPayload,
                        RequestActionPayloadType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestService.addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS,
                pmrvUser.getUserId());
    }
}
