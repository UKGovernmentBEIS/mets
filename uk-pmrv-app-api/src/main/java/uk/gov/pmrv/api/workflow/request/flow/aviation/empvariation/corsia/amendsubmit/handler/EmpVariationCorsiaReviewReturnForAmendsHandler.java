package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.handler;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.mapper.EmpVariationCorsiaAmendSubmitMapper;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@Component
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

	private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final EmpVariationCorsiaReviewService empVariationCorsiaReviewService;
    private final EmpVariationCorsiaReviewReturnForAmendsValidatorService
        empVariationCorsiaReviewReturnForAmendsValidatorService;
    private final WorkflowService workflowService;
    private static final EmpVariationCorsiaAmendSubmitMapper MAPPER = Mappers.getMapper(EmpVariationCorsiaAmendSubmitMapper.class);

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, PmrvUser pmrvUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate that at least one review group is 'Operator to amend'
        empVariationCorsiaReviewReturnForAmendsValidatorService.validate(
        		(EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Update request payload
        empVariationCorsiaReviewService.saveRequestReturnForAmends(requestTask, pmrvUser);

        // Add request action
        createRequestAction(requestTask.getRequest(), pmrvUser, (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(Request request, PmrvUser pmrvUser, EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload) {
        EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload requestActionPayload =
        		MAPPER.toEmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload(taskPayload,
                    RequestActionPayloadType.EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS,
                pmrvUser.getUserId());
    }
}
