package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.mapper.AviationAerUkEtsReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.validation.RequestAviationAerUkEtsReviewValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AviationAerUkEtsReviewReturnForAmendsActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final RequestAviationAerUkEtsReviewValidatorService aerUkEtsReviewValidatorService;
    private final RequestAviationAerUkEtsReviewService aerUkEtsReviewService;
    private final WorkflowService workflowService;

    private final AviationAerUkEtsReviewMapper aviationAerUkEtsReviewMapper;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser,
                        RequestTaskActionEmptyPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = (AviationAerUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate that at least one review group is 'Operator to amend'
        aerUkEtsReviewValidatorService.validateAtLeastOneReviewGroupAmendsNeeded(requestTaskPayload);

        // Update request payload
        aerUkEtsReviewService.updateRequestPayloadWithReviewOutcome(requestTask, pmrvUser);

        // Add request action
        createRequestAction(requestTask, pmrvUser);

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.AVIATION_AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED));

    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(RequestTask requestTask, PmrvUser pmrvUser) {
        Request request = requestTask.getRequest();
        AviationAerUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = (AviationAerUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();

        AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload requestActionPayload =
            aviationAerUkEtsReviewMapper.toAviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload(
                requestTaskPayload,
                RequestActionPayloadType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
            );

        requestService.addActionToRequest(
            request,
            requestActionPayload,
            RequestActionType.AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS,
            pmrvUser.getUserId());
    }

}
